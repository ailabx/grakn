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

package grakn.core.graql.admin;

import grakn.core.graql.concept.SchemaConcept;
import grakn.core.graql.concept.Type;
import grakn.core.graql.query.pattern.Var;

import java.util.Set;

/**
 * Interface for defining unifier comparisons.
 */
public interface UnifierComparison {

    /**
     * @return true if types should be inferred when computing unifier
     */
    boolean inferTypes();

    /**
     * @param parent
     * @param child
     * @return
     */
    boolean typeExplicitenessCompatibility(Atomic parent, Atomic child);

    /**
     * @param parent {@link SchemaConcept} of parent expression
     * @param child  {@link SchemaConcept} of child expression
     * @return true if {@link Type}s are compatible
     */
    boolean typeCompatibility(SchemaConcept parent, SchemaConcept child);

    /**
     * @param parent {@link Atomic} of parent expression
     * @param child  {@link Atomic} of child expression
     * @return true if id predicates are compatible
     */
    boolean idCompatibility(Atomic parent, Atomic child);

    /**
     * @param parent {@link Atomic} of parent expression
     * @param child  {@link Atomic} of child expression
     * @return true if value predicates are compatible
     */
    boolean valueCompatibility(Atomic parent, Atomic child);

    /**
     * @param query to be checked
     * @param var   variable of interest
     * @param type  which playability is toto be checked
     * @return true if typing the typeVar with type is compatible with role configuration of the provided query
     */
    boolean typePlayability(ReasonerQuery query, Var var, Type type);

    /**
     * @param parent multipredicate of parent attribute
     * @param child  multipredicate of child attribute
     * @return true if multipredicates of attributes are compatible
     */
    default boolean attributeValueCompatibility(Set<Atomic> parent, Set<Atomic> child) {
        //checks intra-vp compatibility
        return (child.isEmpty() || child.stream().allMatch(cp -> child.stream().allMatch(cp::isCompatibleWith)))
                && (parent.isEmpty() || parent.stream().allMatch(cp -> parent.stream().allMatch(cp::isCompatibleWith)));
    }

    /**
     * @param parent    {@link Atomic} query
     * @param child     {@link Atomic} query
     * @param parentVar variable of interest in the parent query
     * @param childVar  variable of interest in the child query
     * @return true if attributes attached to child var are compatible with attributes attached to parent var
     */
    default boolean attributeCompatibility(ReasonerQuery parent, ReasonerQuery child, Var parentVar, Var childVar) { return true;}
}
