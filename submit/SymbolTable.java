package submit;

import java.util.*;

/*
 * Code formatter project
 * CS 4481
 */
/**
 *
 */
public class SymbolTable {

  private final HashMap<String, SymbolInfo> table;
  private SymbolTable parent;
  private final List<SymbolTable> children;
  private static int labelCounter = 0;
  private int activationRecordSize;



  public SymbolTable() {
    table = new HashMap<>();
    parent = null;
    children = new ArrayList<>();

    this.addSymbol("println", new SymbolInfo("println"));
  }
  public void addSymbol(String id, SymbolInfo symbol) {
    if (!symbol.isFunction()){
      activationRecordSize = activationRecordSize + 4;
      symbol.setOffset(activationRecordSize * -1);
    }
    table.put(id, symbol);
  }

  /**
   * Returns null if no symbol with that id is in this symbol table or an
   * ancestor table.
   *
   * @param id
   * @return
   */
  public SymbolInfo find(String id) {
    if (table.containsKey(id)) {
      return table.get(id);
    }
    if (parent != null) {
      return parent.findInParent(id, parent.getActivationRecordSize());
    }
    return null;
  }

  /**
   * Returns the new child.
   *
   * @return
   */
  public SymbolTable createChild() {
    SymbolTable child = new SymbolTable();
    children.add(child);
    child.parent = this;
    return child;
  }

  public SymbolTable getParent() {
    return parent;
  }

  /**
   * Generates a unique label for use in MIPS assembly code
   * @return A unique label string
   */
  public static String getUniqueLabel() {
    return "label_" + (labelCounter++);
  }

  /**
   * Gets the current size of the activation record
   * @return The size in bytes
   */
  public int getActivationRecordSize() {
    return activationRecordSize;
  }

  private SymbolInfo findInParent(String id, int baseOffset) {
    if (table.containsKey(id)) {
      SymbolInfo target = table.get(id);
      SymbolInfo symbolWithUpdatedOffset = new SymbolInfo(id, target.getType(), target.isFunction());
      if (!target.isFunction()) {
        symbolWithUpdatedOffset.setOffset(target.getOffset() + baseOffset);
      }
      return symbolWithUpdatedOffset;
    }
    if (parent != null) {
      return parent.findInParent(id, baseOffset + parent.getActivationRecordSize());
    }
    return null;
  }

  public HashMap<String, SymbolInfo> getTable() {
    return table;
  }

}
