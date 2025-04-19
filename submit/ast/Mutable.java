/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;

/**
 *
 * @author edwajohn
 */
public class Mutable extends AbstractNode implements Expression, Node {

  private final String id;
  private final Expression index;

  public Mutable(String id, Expression index) {
    this.id = id;
    this.index = index;
  }

  public String getId() {
    return id;
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
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data, SymbolTable symbolTable, RegisterAllocator regAllocator) {
    SymbolInfo symbolInfo = symbolTable.find(id);
    int targetOffset = symbolInfo.getOffset();
    String reg = regAllocator.getT();

    if (reg == null) {
      System.err.println("Couldnt find reg for mutable");
    }

    code.append("# Get ").append(id).append("'s offset from $sp from the symbol table and initialize\n");
    code.append("li ").append(reg).append(" ").append(targetOffset).append("\n");

    code.append("# Add the stack pointer address to the offset.\n");
    code.append("add ").append(reg).append(" $sp ").append(reg).append("\n");
    code.append("# Load the value of ").append(id).append(".\n");
    code.append("lw ").append(reg).append(" 0(").append(reg).append(")\n");

    return MIPSResult.createRegisterResult(reg, symbolInfo.getType());
  }
}
