/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import java.util.List;
import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolInfo;
import submit.SymbolTable;


/**
 *
 * @author edwajohn
 */
public class CompoundStatement extends AbstractNode implements Statement {

  private final List<Statement> statements;

  public CompoundStatement(List<Statement> statements) {
    this.statements = statements;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(prefix).append("{\n");
    for (Statement s : statements) {
      s.toCminus(builder, prefix + "  ");
    }
    builder.append(prefix).append("}\n");
  }

  @Override
  public MIPSResult toMIPS(StringBuilder code, StringBuilder data,
                           SymbolTable parentSymbolTable, RegisterAllocator regAllocator) {
    // Create new child scope
    SymbolTable symbolTable = parentSymbolTable.createChild();

    // Process declarations first to set up scope
    for (Statement s : statements) {
      if (s instanceof VarDeclaration) {
        s.toMIPS(code, data, symbolTable, regAllocator);
      }
    }

    // Print scope info
    code.append("# Entering a new scope.\n");
    code.append("# Symbols in symbol table:\n");
    for (String symbol : symbolTable.getSymbols()) {
      code.append("#  ").append(symbol).append("\n");
    }

    // Adjust stack pointer for this scope
    int scopeSize = symbolTable.getCurrentScopeSize();
    if (scopeSize > 0) {
      code.append("# Update the stack pointer.\n");
      code.append("addi $sp $sp -").append(scopeSize).append("\n");
    }

    // Process other statements
    for (Statement s : statements) {
      if (!(s instanceof VarDeclaration)) {
        s.toMIPS(code, data, symbolTable, regAllocator);
      }
    }

    // Clean up scope
    if (scopeSize > 0) {
      code.append("# Exiting scope.\n");
      code.append("addi $sp $sp ").append(scopeSize).append("\n");
    }

    return MIPSResult.createVoidResult();
  }
}
