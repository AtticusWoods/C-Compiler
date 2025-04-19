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
public class Assignment extends AbstractNode implements Expression, Node {

  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    mutable.toCminus(builder, prefix);
    if (rhs != null) {
      builder.append(" ").append(type.toString()).append(" ");
      rhs.toCminus(builder, prefix);
    } else {
      builder.append(type.toString());

    }
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code,
                           StringBuilder data,
                           SymbolTable symbolTable,
                           RegisterAllocator regAllocator) {

    SymbolInfo symbolInfo = symbolTable.find(mutable.getId());
    int targetOffset = symbolInfo.getOffset();
    String reg = regAllocator.getT();
    if (reg == null) {
      System.err.println("Assignment reg failure");
    }
    code.append("# Get ").append(mutable.getId()).append("' offset from $sp from the symbol table and initialize x's address with it. We'll add $sp later.\n");
    code.append("li ").append(reg).append(targetOffset).append("\n");

    code.append("# Add the stack pointer address to the offset.").append("\n");
    code.append(String.format("add %s $sp %s\n", reg, reg));

    code.append("# Compute rhs for assignment\n");
    MIPSResult expressionMips = rhs.toMIPS(code, data, symbolTable, regAllocator); // returns reg with result of rhs

    code.append("# Complete assignment statement with store\n");
    code.append(String.format("sw %s 0(%s)\n", expressionMips.getRegister(), reg));

    regAllocator.clear(expressionMips.getRegister());
    regAllocator.clear(reg);

    return MIPSResult.createVoidResult();
//    return MIPSResult.createRegisterResult(reg, expressionMips.getType());
  }

}
