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
    // Always use the existing parent symbol table - this flattens the scope hierarchy
    SymbolTable symbolTable = parentSymbolTable;
    
    // Process all statements in the scope without re-adding variables
    // as they've already been added in FunDeclaration.extractVariableDeclarations
    for (Statement s : statements) {
      if (!(s instanceof VarDeclaration)) {
        s.toMIPS(code, data, symbolTable, regAllocator);
      }
    }

    return MIPSResult.createVoidResult();
  }

  public List<Statement> getStatements() {
    return statements;
  }
}
