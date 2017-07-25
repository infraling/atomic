/**
 * 
 */
package org.corpus_tools.atomic.tagset.impl;

import org.corpus_tools.atomic.models.AbstractBean;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * A simple implementation of a {@link TagsetValue}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
@JsonDeserialize(as = JavaTagsetValueImpl.class)
public class JavaTagsetValueImpl extends AbstractBean implements TagsetValue {

	private String layer;
	private SALT_TYPE elementType;
	private String namespace;
	private String name;
	private String value;
	private boolean regularExpression;
	private String description;
	
	/**
	 * Default constructor, needed to adhere to Java Bean specs.
	 */
	public JavaTagsetValueImpl() {
		// Do nothing, needed for serialization/deserialization
	}

	/**
	 * // TODO Add description
	 * 
	 * @param layer
	 * @param elementType
	 * @param namespace
	 * @param name
	 * @param value
	 * @param regularExpression
	 * @param description
	 */
	public JavaTagsetValueImpl(String layer, SALT_TYPE elementType, String namespace, String name, String value,
			boolean regularExpression, String description) {
				this.layer = layer;
				this.elementType = elementType;
				this.namespace = namespace;
				this.name = name;
				this.value = value;
				this.regularExpression = regularExpression;
				this.description = description;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		String oldValue = this.value;
		this.value = value;
		firePropertyChange("value", oldValue, this.value);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
		String oldDescription = this.description;
		this.description = description;
		firePropertyChange("description", oldDescription, this.description);
	}

	/* (non-Javadoc)
	 * @see org.corpus_tools.atomic.tagset.TagsetValue#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setLayer(String layerName) {
		String oldLayer = this.layer;
		this.layer = layerName;
		firePropertyChange("layer", oldLayer, this.layer);
	}

	@Override
	public String getLayer() {
		return layer;
	}

	@Override
	public void setElementType(SALT_TYPE elementType) {
		SALT_TYPE oldElementType = this.elementType;
		this.elementType = elementType;
		firePropertyChange("elementType", oldElementType, this.elementType);
	}

	@Override
	public SALT_TYPE getElementType() {
		return elementType;
	}

	@Override
	public void setNamespace(String annotationNamespace) {
		String oldNamespace = this.namespace;
		this.namespace = annotationNamespace;
		firePropertyChange("namespace", oldNamespace, this.namespace);
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public void setName(String annotationName) {
		String oldName = this.name;
		this.name = annotationName;
		firePropertyChange("name", oldName, this.name);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isRegularExpression() {
		return regularExpression;
	}

	@Override
	public void setRegularExpression(boolean regularExpression) {
		boolean oldRegularExpression = this.regularExpression;
		this.regularExpression = regularExpression;
		firePropertyChange("regularExpression", oldRegularExpression, this.regularExpression);
	}
	
	@Override
	public String toString() {
		return this.getValue() + " (" + this.getDescription() + ")\n" +
				"\t(Layer name: " + getLayer() + "), " +
				"(Element type: " + getElementType() + "), " +
				"(Namespace: " + getNamespace() + "), " +
				"(Annotation name: " + getName() + ")";
	}

}
