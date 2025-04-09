package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;

public class Assignment extends AbstractNode implements Expression {
  private final Mutable mutable;
  private final AssignmentType type;
  private final Expression rhs;

  public Assignment(Mutable mutable, String assign, Expression rhs) {
    this.mutable = mutable;
    this.type = AssignmentType.fromString(assign);
    this.rhs = rhs;
  }

  @Override
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
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable symbolTable, RegisterAllocator regAllocator) {
    int offset = symbolTable.getOffset(mutable.getId());
    String addrReg = regAllocator.getAny();

    // Exact comment format as requested
    code.append("# Get ").append(mutable.getId())
            .append("'s offset from $sp from the symbol table and initialize ")
            .append(mutable.getId()).append("'s address with it. We'll add $sp later.\n");
    code.append("li ").append(addrReg).append(" ").append(offset).append("\n");
    code.append("# Add the stack pointer address to the offset.\n");
    code.append("add ").append(addrReg).append(" ").append(addrReg).append(" $sp\n");

    // Handle RHS with exact comments
    code.append("# Compute rhs for assignment =\n");
    MIPSResult rhsResult = rhs.toMIPS(code, data, symbolTable, regAllocator);
    String valReg = rhsResult.getRegister();

    if (valReg == null) {
      valReg = regAllocator.getAny();
      code.append("lw ").append(valReg).append(" ").append(rhsResult.getAddress()).append("\n");
    }

    code.append("# complete assignment statement with store\n");
    code.append("sw ").append(valReg).append(" 0(").append(addrReg).append(")\n");

    regAllocator.clear(valReg);
    regAllocator.clear(addrReg);
    return MIPSResult.createVoidResult();
  }
}
