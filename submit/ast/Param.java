/*
 * Code formatter project
 * CS 4481
 */
package submit.ast;

/**
 *
 * @author edwajohn
 */
public class Param extends AbstractNode implements Node {

  private final VarType type;
  private final String id;
  private final boolean isArray;

  public Param(VarType type, String id, boolean isArray) {
    this.type = type;
    this.id = id;
    this.isArray = isArray;
  }

  public VarType getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public boolean isArray() {
    return isArray;
  }

  @Override
  public void toCminus(StringBuilder builder, String prefix) {
    builder.append(type).append(" ").append(id);
    if (isArray) {
      builder.append("[]");
    }
  }
}
