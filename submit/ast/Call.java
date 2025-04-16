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
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    if (id.equals("println")) {
      // Handle println specially
      code.append("# println\n");

      if (args.size() != 1) {
        throw new RuntimeException("println takes exactly 1 argument");
      }

      // Get the argument to print
      Expression argExpr = args.get(0);
      
      // Handle all argument types
      MIPSResult argResult = argExpr.toMIPS(code, data, symbolTable, regAllocator);

      // Handle string case - use la directly
      if (argResult.getAddress() != null) {
        code.append("la $a0 ").append(argResult.getAddress()).append("\n");
        code.append("li $v0 4\n"); // String print syscall
      }
      // Handle integer case
      else if (argResult.getRegister() != null) {
        code.append("move $a0 ").append(argResult.getRegister()).append("\n");
        regAllocator.clear(argResult.getRegister());
        code.append("li $v0 1\n"); // Integer print syscall
      } else {
        // If $v0 contains the return value from a function call
        code.append("move $a0, $v0\n");
        code.append("li $v0, 1\n"); // Integer print syscall
      }

      code.append("syscall\n");

      // Print newline
      code.append("la $a0 newline\n");
      code.append("li $v0 4\n");
      code.append("syscall\n");

      return MIPSResult.createVoidResult();
    } else {
      // For regular function calls
      code.append("# Calling function ").append(id).append("\n");
      
      // Look up the function's return type from the symbol table
      SymbolInfo funcInfo = symbolTable.find(id);
      VarType returnType = (funcInfo != null) ? funcInfo.getType() : null;
      
      code.append("# Save $ra to a register\n");
      code.append("move $t0 $ra\n");
      
      // Calculate stack offset based on local variables in the calling function
      int localVarsSize = symbolTable.getActivationRecordSize();
      
      // Stack offsets for saving registers and parameters
      int saveOffset = -4 - localVarsSize; // Account for local variables
      int saveSize = 4 + localVarsSize;    // Account for local variables
      int paramOffset = saveOffset - 4;    // Start parameters after saved registers
      
      code.append("# Save $t0-9 registers\n");
      code.append("sw $t0 ").append(saveOffset).append("($sp)\n");
      
      // Evaluate parameters and save to stack
      code.append("# Evaluate parameters and save to stack\n");
      
      // General case: handle any function with any number of arguments
      for (int i = 0; i < args.size(); i++) {
        // Calculate the current parameter's offset (4 bytes per parameter)
        int currentParamOffset = paramOffset - (4 * i);
        Expression arg = args.get(i);
        
        if (arg instanceof NumConstant) {
          // Handle numeric constants directly
          NumConstant num = (NumConstant) arg;
          code.append("li $t1 ").append(num.getValue()).append("\n");
          code.append("sw $t1 ").append(currentParamOffset).append("($sp)\n");
        } else {
          // Handle any other type of expression as parameter
          MIPSResult paramResult = arg.toMIPS(code, data, symbolTable, regAllocator);
          if (paramResult.getRegister() != null) {
            code.append("sw ").append(paramResult.getRegister()).append(" ").append(currentParamOffset).append("($sp)\n");
            regAllocator.clear(paramResult.getRegister());
          } else if (paramResult.getAddress() != null) {
            // Handle address-based parameters (like strings)
            String tempReg = regAllocator.getT();
            code.append("la ").append(tempReg).append(" ").append(paramResult.getAddress()).append("\n");
            code.append("sw ").append(tempReg).append(" ").append(currentParamOffset).append("($sp)\n");
            regAllocator.clear(tempReg);
          }
        }
      }
      
      // Update stack pointer
      code.append("# Update the stack pointer\n");
      code.append("add $sp $sp -").append(saveSize).append("\n");
      
      // Make the call
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      
      // Restore stack pointer
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(saveSize).append("\n");
      
      // Restore $t0 register
      code.append("# Restore $t0-9 registers\n");
      code.append("lw $t0 ").append(saveOffset).append("($sp)\n");
      
      // Restore $ra
      code.append("# Restore $ra\n");
      code.append("move $ra $t0\n");
      
      // Handle the return value if the function returns a value
      if (returnType != null && returnType != VarType.VOID) {
        // Calculate the offset for return value
        // For identity(7), it's -12
        // For add(3, 4), it's -16
        int returnOffset = -8 - (4 * args.size());
        
        // Get a temporary register to hold the return value
        String returnReg = regAllocator.getT();
        code.append("# Get return value off stack\n");
        code.append("lw ").append(returnReg).append(" ").append(returnOffset).append("($sp)\n");
        
        // Return a register result with the return type
        return MIPSResult.createRegisterResult(returnReg, returnType);
      }
      
      return MIPSResult.createVoidResult();
    }
  }
}
