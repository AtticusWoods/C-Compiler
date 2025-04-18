/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

import submit.MIPSResult;
import submit.RegisterAllocator;
import submit.SymbolTable;
import submit.SymbolInfo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author edwajohn
 */
public class VarDeclaration extends AbstractNode implements Declaration {

  private final VarType type;
  private final List<String> ids;
  private final List<Integer> arraySizes;
  private final boolean isStatic;

  public VarDeclaration(VarType type, List<String> ids, List<Integer> arraySizes, boolean isStatic) {
    this.type = type;
    this.ids = new ArrayList<>(ids);
    this.arraySizes = arraySizes;
    this.isStatic = isStatic;
  }

  public void toCminus(StringBuilder builder, final String prefix) {
    builder.append(prefix);
    if (isStatic) {
      builder.append("static ");
    }
    builder.append(type).append(" ");
    for (int i = 0; i < ids.size(); ++i) {
      final String id = ids.get(i);
      final int arraySize = arraySizes.get(i);
      if (arraySize >= 0) {
        builder.append(id).append("[").append(arraySize).append("]").append(", ");
      } else {
        builder.append(id).append(", ");
      }
    }
    builder.delete(builder.length() - 2, builder.length());
    builder.append(";\n");
  }
  
  @Override
  public MIPSResult toMIPS(StringBuilder code,
                         StringBuilder data,
                         SymbolTable symbolTable,
                         RegisterAllocator regAllocator) {
    // For each variable declaration, assign it an appropriate offset in the stack
    for (int i = 0; i < ids.size(); i++) {
      String id = ids.get(i);
      int arraySize = arraySizes.get(i);
      
      // Find the symbol in the table
      SymbolInfo symbolInfo = symbolTable.find(id);
      if (symbolInfo != null) {
        int size = 4; // Default size for primitives (int, bool, char)
        
        // If this is an array, calculate its total size
        if (arraySize > 0) {
          size = 4 * arraySize; // Each element is 4 bytes
        }
        
        // Calculate the offset for this variable (negative because stack grows downward)
        int offset = symbolTable.getActivationRecordSize() * -1 - size;
        
        // Update the symbol info with the size and offset
        symbolInfo.setSize(size);
        symbolInfo.setOffset(offset);
        
        // Update the activation record size
        symbolTable.addToActivationRecordSize(size);
        
        // No actual MIPS code is generated here, just updating symbol table
      }
    }
    
    return MIPSResult.createVoidResult();
  }

}
