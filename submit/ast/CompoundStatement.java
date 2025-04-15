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
    
    SymbolTable symbolTable = parentSymbolTable;
    
    // Calculate total size needed for this scope's variables
    int scopeSize = 0;

    // First pass - add all variables to the symbol table
    for (Statement s : statements) {
      if (s instanceof VarDeclaration) {
        VarDeclaration varDecl = (VarDeclaration) s;
        for (String id : varDecl.getIds()) {
          scopeSize += 4; // Each variable takes 4 bytes
          symbolTable.addVariable(id, varDecl.getType());
        }
      }
    }

    // Process all statements in the scope
    for (Statement s : statements) {
      s.toMIPS(code, data, symbolTable, regAllocator);
    }

    return MIPSResult.createVoidResult();
  }

  public List<Statement> getStatements() {
    return statements;
  }
}
