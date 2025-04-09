package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

public class Mutable extends AbstractNode implements Expression, Node {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(id);
    if (index != null) {
      builder.append("[");
      index.toCminus(builder, prefix);
      builder.append("]");
    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    String resultReg = regAllocator.getAny();

    int offset = symbolTable.getOffset(id);

    // Generate comments for clarity in assembly code
    code.append("# Get " + id + "'s offset from $sp from the symbol table and initialize "
                + id + "'s address with it. We'll add $sp later.\n");
    code.append("li ").append(resultReg).append(" ").append(offset).append("\n");
    code.append("# Add the stack pointer address to the offset.\n");
    code.append("add ").append(resultReg).append(" ").append(resultReg).append(" $sp\n");
    code.append("# Load the value of " + id + ".\n");
    code.append("lw ").append(resultReg).append(" 0(").append(resultReg).append(")\n");

    return MIPSResult.createRegisterResult(resultReg, VarType.INT);
  }

  public String getId() {
    return id;
  }
}
