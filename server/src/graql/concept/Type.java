/*
 * GRAKN.AI - THE KNOWLEDGE GRAPH
 * Copyright (C) 2018 Grakn Labs Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.core.graql.concept;

import grakn.core.server.exception.TransactionException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.util.stream.Stream;

/**
 * <p>
 *     A Type represents any ontological element in the graph.
 * </p>
 *
 * <p>
 *     Types are used to model the behaviour of {@link Thing} and how they relate to each other.
 *     They also aid in categorising {@link Thing} to different types.
 * </p>
 *
 * @see EntityType
 * @see Role
 * @see RelationshipType
 * @see AttributeType
 *
 *
 */
public interface Type extends SchemaConcept {
    //------------------------------------- Modifiers ----------------------------------
    /**
     * Changes the {@link Label} of this {@link Concept} to a new one.
     * @param label The new {@link Label}.
     * @return The {@link Concept} itself
     */
    Type label(Label label);

    /**
     * Sets the Entity Type to be abstract - which prevents it from having any instances.
     *
     * @param isAbstract  Specifies if the concept is to be abstract (true) or not (false).
     * @return The concept itself
     *
     * @throws TransactionException if this is a meta-type
     */
    Type isAbstract(Boolean isAbstract) throws TransactionException;

    /**
     *
     * @param role The Role Type which the instances of this Type are allowed to play.
     * @return The Type itself.
     *
     * @throws TransactionException if this is a meta-type
     */
    Type plays(Role role) throws TransactionException;

    /**
     * Creates a {@link RelationshipType} which allows this type and a {@link AttributeType} to be linked in a strictly one-to-one mapping.
     *
     * @param attributeType The {@link AttributeType} which instances of this type should be allowed to play.
     * @return The Type itself.
     *
     * @throws TransactionException if this is a meta-type
     */
    Type key(AttributeType attributeType) throws TransactionException;

    /**
     * Creates a {@link RelationshipType} which allows this type and a {@link AttributeType}  to be linked.
     *
     * @param attributeType The {@link AttributeType}  which instances of this type should be allowed to play.
     * @return The Type itself.
     *
     * @throws TransactionException if this is a meta-type
     */
     Type has(AttributeType attributeType) throws TransactionException;

    //------------------------------------- Accessors ---------------------------------

    /**
     *
     * @return A list of Role Types which instances of this Type can indirectly play.
     */
    Stream<Role> playing();

    /**
     *
     * @return The {@link AttributeType}s which this {@link Type} is linked with.
     */
    @CheckReturnValue
    Stream<AttributeType> attributes();

    /**
     *
     * @return The {@link AttributeType}s which this {@link Type} is linked with as a key.
     */
    @CheckReturnValue
    Stream<AttributeType> keys();

    /**
     *
     * @return The direct super of this Type
     */
    @CheckReturnValue
    @Nullable
    Type sup();

    /**
     *
     * @return All the the super-types of this {@link Type}
     */
    @Override
    Stream<? extends Type> sups();

    /**
     * Get all indirect sub-types of this type.
     *
     * The indirect sub-types are the type itself and all indirect sub-types of direct sub-types.
     *
     * @return All the indirect sub-types of this Type
     */
    @CheckReturnValue
    Stream<? extends Type> subs();

    /**
     * Get all indirect instances of this type.
     *
     * The indirect instances are the direct instances and all indirect instances of direct sub-types.
     *
     * @return All the indirect instances of this type.
     */
    @CheckReturnValue
    Stream<? extends Thing> instances();

    /**
     * Return if the type is set to abstract.
     *
     * By default, types are not abstract.
     *
     * @return returns true if the type is set to be abstract.
     */
    @CheckReturnValue
    Boolean isAbstract();

    //------------------------------------- Other ----------------------------------
    /**
     * Removes the ability of this {@link Type} to play a specific {@link Role}
     *
     * @param role The {@link Role} which the {@link Thing}s of this {@link Type} should no longer be allowed to play.
     * @return The {@link Type} itself.
     */
    Type unplay(Role role);

    /**
     * Removes the ability for {@link Thing}s of this {@link Type} to have {@link Attribute}s of type {@link AttributeType}
     *
     * @param attributeType the {@link AttributeType} which this {@link Type} can no longer have
     * @return The {@link Type} itself.
     */
    Type unhas(AttributeType attributeType);

    /**
     * Removes {@link AttributeType} as a key to this {@link Type}
     *
     * @param attributeType the {@link AttributeType} which this {@link Type} can no longer have as a key
     * @return The {@link Type} itself.
     */
    Type unkey(AttributeType attributeType);

    @Deprecated
    @CheckReturnValue
    @Override
    default Type asType(){
        return this;
    }

    @Deprecated
    @CheckReturnValue
    @Override
    default boolean isType(){
        return true;
    }
}
