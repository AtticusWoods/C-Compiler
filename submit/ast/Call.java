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
      // Only save actually used registers
      List<String> usedRegs = regAllocator.getUsed();
      int saveSize = usedRegs.size() * 4;

      // Save $ra
      String tempReg = regAllocator.getAny();
      code.append("# Calling function ").append(id).append("\n");
      code.append("# Save $ra to a register\n");
      code.append("move ").append(tempReg).append(" $ra\n");

      // Save used registers
      if (!usedRegs.isEmpty()) {
        code.append("# Save used registers\n");
        int offset = -4;
        for (String reg : usedRegs) {
          code.append("sw ").append(reg).append(" ").append(offset).append("($sp)\n");
          offset -= 4;
        }
      }

      // Adjust stack pointer
      code.append("# Update the stack pointer\n");
      code.append("addi $sp $sp ").append(-saveSize).append("\n");

      // Make the call
      code.append("# Call the function\n");
      code.append("jal ").append(id).append("\n");

      // Restore stack pointer
      code.append("# Restore the stack pointer\n");
      code.append("addi $sp $sp ").append(saveSize).append("\n");

      // Restore used registers
      if (!usedRegs.isEmpty()) {
        code.append("# Restore used registers\n");
        int offset = -4;
        for (String reg : usedRegs) {
          code.append("lw ").append(reg).append(" ").append(offset).append("($sp)\n");
          offset -= 4;
        }
      }

      // Restore $ra
      code.append("# Restore $ra\n");
      code.append("move $ra ").append(tempReg).append("\n");

      regAllocator.clear(tempReg);
    }
    return MIPSResult.createVoidResult();
//    throw new UnsupportedOperationException("Function calls not yet implemented");
  }
}
