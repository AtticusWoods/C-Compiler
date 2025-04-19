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
      // Handle regular function calls with parameters
      code.append("# Calling function ").append(id).append("\n");

      // First, get a dedicated register for saving $ra that won't be overwritten
      String raRegister = regAllocator.getT();
      code.append("# Save $ra to a dedicated register\n");
      code.append("move ").append(raRegister).append(" $ra\n");
      
      // Calculate initial stack space needed for base register saving
      symbolTable.addToActivationRecordSize(4);  // For $ra initially
      int baseOffset = symbolTable.getActivationRecordSize();

      // Save all used temporary registers BEFORE evaluating any parameters
      code.append("# Save used temporary registers\n");
      int regSaveSpace = regAllocator.saveT(code, baseOffset);
      
      // Update activation record size to include space for saved registers
      if (regSaveSpace > 0) {
        symbolTable.addToActivationRecordSize(regSaveSpace);
      }
      
      // Get total space needed for saved registers
      int savedRegsSpace = symbolTable.getActivationRecordSize();
      
      // Now evaluate parameters and save to stack (after saving registers)
      code.append("# Evaluate parameters and save to stack\n");

      for (int i = 0; i < args.size(); i++) {
        symbolTable.addToActivationRecordSize(4);
        int paramOffset = symbolTable.getActivationRecordSize();
        Expression arg = args.get(i);
        MIPSResult result = arg.toMIPS(code, data, symbolTable, regAllocator);

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
      symbolTable.addToActivationRecordSize(-4 * args.size());

      // Update stack pointer for function call
      code.append("# Update the stack pointer\n");
      code.append("add $sp $sp -").append(savedRegsSpace).append("\n");

      // Call the function
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");

      // Restore stack pointer
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(savedRegsSpace).append("\n");

      // Restore used temporary registers
      code.append("# Restore used temporary registers\n");
      regAllocator.restoreT(code, baseOffset);
      
      // Account for stack space used by saved registers
      symbolTable.addToActivationRecordSize(-savedRegsSpace);

      // Restore return address from our dedicated register
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(raRegister).append("\n");
      regAllocator.clear(raRegister);
      
      // Get the return value from the stack using the special return symbol location
      // We need to calculate where the return value is stored in relation to the current stack pointer
      int paramSize = 4 * args.size(); // Size of all parameters
      int returnOffset = savedRegsSpace + paramSize + 4; // +4 for the return value itself
      
      // Retrieve the return value and store it in $t0
      code.append("# Get return value off stack\n");
      code.append("lw $t0 -").append(returnOffset).append("($sp)\n");
      
      // Return the result in $t0
      String resultReg = "$t0";  // Always use t0 for consistency with other parts of the compiler
      
      // Get the function's return type from the symbol table
      SymbolInfo funcInfo = symbolTable.find(id);
      VarType returnType = funcInfo != null ? funcInfo.getType() : VarType.INT;
      
      return MIPSResult.createRegisterResult(resultReg, returnType);
    }
  }
}
