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
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {
    // Handle special case for println function
    if (id.equals("println")) {
      // Ensure we have a newline defined in the data section
      // Do this at the beginning to ensure it's always defined
      if (!data.toString().contains("newline:")) {
        data.append("newline:\t.asciiz\t\"\\n\"\n");
      }
      
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
      // Handle regular function calls
      code.append("# Calling function ").append(id).append("\n");
      
      // Save the return address register
      code.append("# Save $ra to a register\n");
      code.append("move $t0 $ra\n");
      
      // Calculate stack space needed for saving registers and arguments
      int stackSpace = 4; // At minimum, we need space to save $ra
      
      // Save $t0 (which now contains $ra)
      code.append("# Save $t0-9 registers\n");
      code.append("sw $t0 -").append(stackSpace).append("($sp)\n");
      
      // Process function arguments if there are any
      code.append("# Evaluate parameters and save to stack\n");
      
      // Adjust stack pointer to make space for saved registers and arguments
      code.append("# Update the stack pointer\n");
      code.append("add $sp $sp -").append(stackSpace).append("\n");
      
      // Call the function
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");
      
      // Restore the stack pointer
      code.append("# Restore the stack pointer\n");
      code.append("add $sp $sp ").append(stackSpace).append("\n");
      
      // Restore the saved registers
      code.append("# Restore $t0-9 registers\n");
      code.append("lw $t0 -").append(stackSpace).append("($sp)\n");
      
      // Restore the return address register
      code.append("# Restore $ra\n");
      code.append("move $ra $t0\n");
      
      return MIPSResult.createVoidResult();
    }
  }
}
