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
      // Handle other function calls
      // This will be implemented later for other functions
      return MIPSResult.createVoidResult();
    }
  }
}
