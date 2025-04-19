/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class Call extends AbstractNode implements Expression {

  private final String id;
  private final List<Expression> args;

  public Call(String id, List<Expression> args) {
    this.id = id;
    this.args = new ArrayList<>(args);
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id).append("(");
    for (Expression arg : args) {
      arg.toCminus(builder, prefix);
      builder.append(", ");
    }
    if (!args.isEmpty()) {
      builder.setLength(builder.length() - 2);
    }
    builder.append(")");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    // Handle special case for println function
    if (id.equals("println")) {
      // Ensure we have a newline defined in the data section
      if (!data.toString().contains("newline:")) {
        data.append("newline:\t.asciiz\t\"\\n\"\n");
      }

      // Process println call
      if (args.size() == 1) {
        code.append("# println\n");
        Expression arg = args.get(0);
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);

        // Handle different types of arguments
        if (result.getType() == VarType.CHAR && result.getAddress() != null) {
          // String constant
          code.append("la $a0 ").append(result.getAddress()).append("\n");
          code.append("li $v0 4\n");
          code.append("syscall\n");
        } else {
          // Numeric value or other expression
          if (result.getRegister() != null) {
            code.append("move $a0 ").append(result.getRegister()).append("\n");
            regAllocator.clear(result.getRegister());
          } else if (result.getType() == VarType.INT) {
            // Handle direct integer value
            code.append("li $a0 ").append(result.getIntValue()).append("\n");
          }
          code.append("li $v0 1\n");
          code.append("syscall\n");
        }

        // Add newline
        code.append("la $a0 newline\n");
        code.append("li $v0 4\n");
        code.append("syscall\n");
      } else {
        // Handle println with no arguments - just print a newline
        code.append("# println\n");
        code.append("la $a0 newline\n");
        code.append("li $v0 4\n");
        code.append("syscall\n");
      }

      return MIPSResult.createVoidResult();
    } else {
      // Normal function call
      code.append("# Calling function ").append(id).append("\n");
      
      // Save $ra to a dedicated register - always use $t9 to avoid conflicts
      code.append("# Save $ra to a dedicated register\n");
      code.append("move $t9 $ra\n");
      
      // First, save all in-use temporary registers that will be needed after the call
      code.append("# Save used temporary registers\n");
      
      // We need to track which registers need saving
      int numSavedRegs = 0;
      StringBuilder saveCmds = new StringBuilder();
      
      // Save all used temporary registers (except t9 which we just used for $ra)
      for (int i = 0; i < 9; i++) {
        String reg = "$t" + i;
        if (regAllocator.isInUse(reg)) {
          numSavedRegs++;
          // Calculate the offset for this register
          int offset = 4 * numSavedRegs;
          saveCmds.append("sw ").append(reg).append(" -").append(offset).append("($sp)\n");
        }
      }
      
      // Add the save commands to the code
      code.append(saveCmds);
      
      // Calculate total stack space needed (4 bytes per saved register)
      int frameSpace = 4 * numSavedRegs;
      
      // Now evaluate parameters and save to stack 
      // We'll use dedicated registers for parameter evaluation
      code.append("# Evaluate parameters and save to stack\n");
      
      // Space for parameters starts after the saved registers
      int paramOffset = frameSpace;
      List<Integer> paramOffsets = new ArrayList<>();
      
      // First, evaluate all parameters and get their values in registers
      for (int i = 0; i < args.size(); i++) {
        Expression arg = args.get(i);
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);
        
        // Increment param offset for this parameter
        paramOffset += 4;
        paramOffsets.add(paramOffset);
        
        // Store the parameter on the stack
        if (result.getRegister() != null) {
          code.append("sw ").append(result.getRegister()).append(" -").append(paramOffset).append("($sp)\n");
          regAllocator.clear(result.getRegister());
        } else {
          // For number constants
          String tempReg = regAllocator.getT();
          code.append("li ").append(tempReg).append(" ").append(result.getIntValue()).append("\n");
          code.append("sw ").append(tempReg).append(" -").append(paramOffset).append("($sp)\n");
          regAllocator.clear(tempReg);
        }
      }
      
      // Update stack pointer before call - only count the saved registers space
      // Parameters are passed via stack but we don't adjust $sp for them
      if (frameSpace > 0) {
        code.append("# Update the stack pointer\n");
        code.append("add $sp $sp -").append(frameSpace).append("\n");
      }
      
      // Call function
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      
      // Restore stack pointer
      if (frameSpace > 0) {
        code.append("# Restore the stack pointer\n");
        code.append("add $sp $sp ").append(frameSpace).append("\n");
      }
      
      // After the call, $t0 now contains the return value (due to our return convention)
      // We need to save it temporarily if we have registers to restore
      String returnReg = "$t0"; // Default return register
      
      if (numSavedRegs > 0) {
        // Save the return value temporarily in $t8
        code.append("# Save return value temporarily\n");
        code.append("move $t8 $t0\n");
        
        // Restore saved registers
        code.append("# Restore used temporary registers\n");
        
        // Restore registers in the same order they were saved
        int regIndex = 0;
        for (int i = 0; i < 9; i++) {
          String reg = "$t" + i;
          if (regAllocator.isInUse(reg)) {
            regIndex++;
            // Calculate the offset for this register
            int offset = 4 * regIndex;
            code.append("lw ").append(reg).append(" -").append(offset).append("($sp)\n");
          }
        }
        
        // Restore return address from $t9
        code.append("# Restore $ra\n");
        code.append("move $ra $t9\n");
        
        // Move return value back to $t0
        code.append("move $t0 $t8\n");
      } else {
        // If no registers to restore, just restore $ra
        code.append("# Restore $ra\n");
        code.append("move $ra $t9\n");
      }
      
      // Get the function's return type from the symbol table
      SymbolInfo funcInfo = symbolTable.find(id);
      VarType returnType = funcInfo != null ? funcInfo.getType() : VarType.INT;
      
      // Return the result in the selected register
      return MIPSResult.createRegisterResult(returnReg, returnType);
    }
  }
}
