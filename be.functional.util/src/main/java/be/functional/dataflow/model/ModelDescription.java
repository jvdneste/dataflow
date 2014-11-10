package be.functional.dataflow.model;

import com.google.common.collect.ImmutableList;

public class ModelDescription {

  private final ImmutableList<IPropertyDefinition> _properties;

  public static final ModelDescription EMPTY = new ModelDescription(new IPropertyDefinition[] {});

  protected  ModelDescription(final IPropertyDefinition[] properties) {
    _properties = ImmutableList.copyOf(properties);
  }

  //	public Model createInstance() {
  //		return new Model(this);
  //	}
}
