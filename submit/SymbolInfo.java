/*
 * Code formatter project
 * CS 4481
 */
package submit;

import submit.ast.VarType;

/**
 *
 * @author edwajohn
 */
public class SymbolInfo {

  private final String id;
  // In the case of a function, type is the return type
  private final VarType type;
  private final boolean function;
  private int offset; // Stack offset for variables
  private int size;   // Size in bytes for variables (4 for most primitives)

  public SymbolInfo(String id, VarType type, boolean function) {
    this.id = id;
    this.type = type;
    this.function = function;
    this.offset = 0;
    this.size = 4; // Default size for int, bool, char
  }

  public String getId() {
    return id;
  }

  public VarType getType() {
    return type;
  }

  public boolean isFunction() {
    return function;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  @Override
  public String toString() {
    return "<" + id + ", " + type + '>';
  }

}
