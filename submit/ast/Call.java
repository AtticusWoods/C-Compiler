/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
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

      MIPSResult argResult = args.get(0).toMIPS(code, data, symbolTable, regAllocator);

      // Handle string case - use la directly
      if (argResult.getAddress() != null) {
        code.append("la $a0 ").append(argResult.getAddress()).append("\n");
        code.append("li $v0 4\n");
      }
      // Handle integer case
      else if (argResult.getRegister() != null) {
        code.append("move $a0 ").append(argResult.getRegister()).append("\n");
        regAllocator.clear(argResult.getRegister());
        code.append("li $v0 1\n");
      } else {
        throw new RuntimeException("Invalid argument type for println");
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
      code.append("# Save $ra to a register\n");
      code.append("move $t0 $ra\n");
      
      // Calculate stack offset based on local variables in the calling function
      int localVarsSize = symbolTable.getActivationRecordSize();
      
      // Stack offsets for saving registers and parameters
      int saveOffset = -4 - localVarsSize; // Account for local variables
      int saveSize = 4 + localVarsSize;   // Account for local variables
      
      code.append("# Save $t0-9 registers\n");
      code.append("sw $t0 ").append(saveOffset).append("($sp)\n");
      
      // Evaluate parameters and save to stack
      code.append("# Evaluate parameters and save to stack\n");
      
      // Update stack pointer - include space for local variables
      code.append("# Update the stack pointer\n");
      code.append("add $sp $sp -").append(saveSize).append("\n");
      
      // Make the call
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      
      // Restore stack pointer - include space for local variables
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(saveSize).append("\n");
      
      // Restore $t0 register
      code.append("# Restore $t0-9 registers\n");
      code.append("lw $t0 ").append(saveOffset).append("($sp)\n");
      
      // Restore $ra
      code.append("# Restore $ra\n");
      code.append("move $ra $t0\n");
      
      return MIPSResult.createVoidResult();
    }
  }
}
