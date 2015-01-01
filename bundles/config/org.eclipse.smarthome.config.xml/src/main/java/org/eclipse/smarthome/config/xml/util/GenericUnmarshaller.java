/**
 * Copyright (c) 2014-2015 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.config.xml.util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;


/**
 * The {@link GenericUnmarshaller} is an abstract implementation of the {@code XStream}
 * {@link Converter} interface used to convert XML tags within an XML document into its
 * according objects.
 * <p>
 * <b>Hint:</b> This class only supports unmarshalling functionality.
 * 
 * @author Michael Grammling - Initial Contribution
 *
 * @param <T> the result type of the conversion 
 */
public abstract class GenericUnmarshaller<T> implements Converter {

    private Class<T> clazz;


    /**
     * Creates a new instance of this class with the specified parameter.
     * 
     * @param clazz the class of the result type (must not be null)
     */
    public GenericUnmarshaller(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Returns the class of the result type
     * 
     * @return the class of the result type
     */
    public Class<?> getResultType() {
        return this.clazz;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public final boolean canConvert(Class paramClass) {
        return (clazz == paramClass);
    }

    @Override
    public final void marshal(
            Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

        throw new UnsupportedOperationException();
    }

}
