package submit;

import submit.ast.VarType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

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
  private final Set<Integer> usedTRegisters;
  private final Set<Integer> usedSRegisters;
  private int activationRecordSize;
  private String currentFunctionName;
  private int currentOffset = 0;

  public SymbolTable() {
    table = new HashMap<>();
    parent = null;
    children = new ArrayList<>();
    usedTRegisters = new HashSet<>();
    usedSRegisters = new HashSet<>();
    activationRecordSize = 0;

    // Add println() as a built-in function
    addSymbol("println", new SymbolInfo("println", VarType.VOID, true));
  }


  public void addSymbol(String id, SymbolInfo symbol) {
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
      return parent.find(id);
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
   * Allocates a temporary register ($t0-$t9)
   * @return The register number if available, -1 if all registers are in use
   */
  public int allocateTRegister() {
    for (int i = 0; i < 10; i++) {
      if (!usedTRegisters.contains(i)) {
        usedTRegisters.add(i);
        return i;
      }
    }
    return -1;
  }

  /**
   * Allocates a saved register ($s0-$s9)
   * @return The register number if available, -1 if all registers are in use
   */
  public int allocateSRegister() {
    for (int i = 0; i < 10; i++) {
      if (!usedSRegisters.contains(i)) {
        usedSRegisters.add(i);
        return i;
      }
    }
    return -1;
  }

  /**
   * Frees a temporary register
   * @param regNum The register number to free
   */
  public void freeTRegister(int regNum) {
    usedTRegisters.remove(regNum);
  }

  /**
   * Frees a saved register
   * @param regNum The register number to free
   */
  public void freeSRegister(int regNum) {
    usedSRegisters.remove(regNum);
  }

  /**
   * Adds to the size of the activation record
   * @param size The number of bytes to add
   */
  public void addToActivationRecordSize(int size) {
    activationRecordSize += size;
  }

  /**
   * Gets the current size of the activation record
   * @return The size in bytes
   */
  public int getActivationRecordSize() {
    return activationRecordSize;
  }

  /**
   * Gets the total size of the activation record including all child scopes
   * @return The total size in bytes
   */
  public int getTotalActivationRecordSize() {
    int totalSize = activationRecordSize;
    for (SymbolTable child : children) {
      totalSize += child.getTotalActivationRecordSize();
    }
    return totalSize;
  }

  public Set<String> getSymbols() {
    Set<String> symbols = new HashSet<>(table.keySet());
    // Remove the current function name if present
    symbols.remove(currentFunctionName);
    return symbols;
  }

  // Add this when creating function declarations
  public void setCurrentFunction(String name) {
    this.currentFunctionName = name;
  }

  // Add this method to add variables with offsets
  public void addVariable(String id, VarType type) {
    currentOffset -= 4; // Each variable takes 4 bytes
    table.put(id, new SymbolInfo(id, type, false, currentOffset));
    activationRecordSize += 4;
  }

  public int getOffset(String id) {
    SymbolInfo info = this.find(id);
    if (info == null) {
      throw new RuntimeException("Variable not found: " + id);
    }

    // Variable is in current scope
    if (table.containsKey(id)) {
      return info.getOffset();
    }
    // Variable is in parent scope
    else if (parent != null) {
      return parent.getOffset(id) + this.getCurrentScopeSize();
    }

    throw new RuntimeException("Variable not found in any scope: " + id);
  }

  public int getCurrentScopeSize() {
    // Only count variables declared in this specific scope
    int size = 0;
    for (SymbolInfo info : table.values()) {
      if (!info.isFunction()) {
        size += 4; // Each variable takes 4 bytes
      }
    }
    return size;
  }
}
