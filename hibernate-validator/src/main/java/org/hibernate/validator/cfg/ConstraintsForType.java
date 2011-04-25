/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc. and/or its affiliates, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.validator.cfg;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import org.hibernate.validator.group.DefaultGroupSequenceProvider;
import org.hibernate.validator.util.ReflectionHelper;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Via instances of this class constraints can be configured for a single bean class.
 *
 * @author Hardy Ferentschik
 * @author Gunnar Morling
 * @author Kevin Pollet - SERLI - (kevin.pollet@serli.com)
 */
public final class ConstraintsForType {
	private static final String EMPTY_PROPERTY = "";

	private final ConstraintMapping mapping;
	private final Class<?> beanClass;

	public ConstraintsForType(Class<?> beanClass, ConstraintMapping mapping) {
		this.beanClass = beanClass;
		this.mapping = mapping;
	}

	/**
	 * Adds a new constraint.
	 *
	 * @param definition The constraint definition class.
	 *
	 * @return A constraint definition class allowing to specify additional constraint parameters.
	 */
	public ConstraintsForType constraint(ConstraintDef<?, ?> definition) {
		final Constructor<?> constructor = ReflectionHelper.getConstructor(
				definition.getClass(), Class.class, String.class, ElementType.class, ConstraintMapping.class
		);

		ConstraintDef<?, ?> constraintDefinition = (ConstraintDef<?, ?>) ReflectionHelper.newConstructorInstance(
				constructor, beanClass, EMPTY_PROPERTY, TYPE, mapping
		);
		
		constraintDefinition.parameters.putAll(definition.parameters);
		
		mapping.addConstraintConfig( constraintDefinition );
		
		return this;
	}
	
	public <A extends Annotation> ConstraintsForType constraint(GenericConstraintDef<A> definition) {
		final GenericConstraintDef<A> constraintDefinition = new GenericConstraintDef<A>(
				beanClass, definition.constraintType, EMPTY_PROPERTY, TYPE, mapping
		);
		constraintDefinition.parameters.putAll(definition.parameters);
		
		mapping.addConstraintConfig( constraintDefinition );
			
		return this;
	 }
	
	/**
	 * Defines the default groups sequence for the bean class of this instance.
	 *
	 * @param defaultGroupSequence the default group sequence.
	 *
	 * @return Returns itself for method chaining.
	 */
	public ConstraintsForType defaultGroupSequence(Class<?>... defaultGroupSequence) {
		mapping.addDefaultGroupSequence( beanClass, Arrays.asList( defaultGroupSequence ) );
		return this;
	}

	/**
	 * Defines the default group sequence provider for the bean class of this instance.
	 *
	 * @param defaultGroupSequenceProviderClass The default group sequence provider class.
	 *
	 * @return Returns itself for method chaining.
	 */
	public <T extends DefaultGroupSequenceProvider<?>> ConstraintsForType defaultGroupSequenceProvider(Class<T> defaultGroupSequenceProviderClass) {
		mapping.addDefaultGroupSequenceProvider( beanClass, defaultGroupSequenceProviderClass );
		return this;
	}

	/**
	 * Creates a new {@code ConstraintsForType} in order to define constraints on a new bean type.
	 *
	 * @param type The bean type.
	 *
	 * @return Returns a new {@code ConstraintsForType} instance.
	 */
	public ConstraintsForType type(Class<?> type) {
		return new ConstraintsForType( type, mapping );
	}

	/**
	 * Changes the property for which added constraints apply.
	 * <p>
	 * Until this method is called constraints apply on class level. After calling this method constraints
	 * apply on the specified property with the given access type.
	 * </p>
	 *
	 * @param property The property on which to apply the following constraints (Java Bean notation).
	 * @param type The access type (field/property).
	 *
	 * @return Returns a new {@code ConstraintsForProperty} instance allowing method chaining.
	 */
	public ConstraintsForTypeProperty property(String property, ElementType type) {
		return new ConstraintsForTypeProperty( beanClass, property, type, mapping );
	}

	/**
	 * Changes the method for which added constraints apply.
	 * <p>
	 * Until this method is called constraints apply on class level. After calling this method constraints
	 * apply to the specified method.
	 * </p>
	 *
	 * @param name The method name.
	 * @param parameterTypes The method parameter types.
	 *
	 * @return Returns a new {@code ConstraintsForMethod} instance allowing method chaining.
	 */
	public ConstraintsForTypeMethod method(String name, Class<?>... parameterTypes) {
		return new ConstraintsForTypeMethod( beanClass, name, parameterTypes, mapping );
	}
}


