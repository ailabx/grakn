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

package grakn.core.graql.internal.reasoner.atom.binary;

import grakn.core.graql.concept.ConceptId;
import grakn.core.graql.query.pattern.Var;
import grakn.core.graql.query.pattern.VarPattern;
import grakn.core.graql.admin.Atomic;
import grakn.core.graql.admin.ReasonerQuery;
import grakn.core.graql.query.pattern.property.VarProperty;
import grakn.core.graql.query.pattern.property.SubProperty;
import grakn.core.graql.internal.reasoner.atom.predicate.Predicate;
import com.google.auto.value.AutoValue;

import java.util.stream.Collectors;

/**
 *
 * <p>
 * TypeAtom corresponding to graql a {@link SubProperty} property.
 * </p>
 *
 *
 */
@AutoValue
public abstract class SubAtom extends OntologicalAtom {

    @Override public abstract Var getPredicateVariable();
    @Override public abstract VarPattern getPattern();
    @Override public abstract ReasonerQuery getParentQuery();

    public static SubAtom create(Var var, Var predicateVar, ConceptId predicateId, ReasonerQuery parent) {
        return new AutoValue_SubAtom(var, predicateId, predicateVar, var.sub(predicateVar), parent);
    }

    private static SubAtom create(SubAtom a, ReasonerQuery parent) {
        return create(a.getVarName(), a.getPredicateVariable(), a.getTypeId(), parent);
    }

    @Override
    OntologicalAtom createSelf(Var var, Var predicateVar, ConceptId predicateId, ReasonerQuery parent) {
        return SubAtom.create(var, predicateVar, predicateId, parent);
    }

    @Override
    public Atomic copy(ReasonerQuery parent){
        return create(this, parent);
    }

    @Override
    public Class<? extends VarProperty> getVarPropertyClass() {return SubProperty.class;}

    @Override
    public String toString(){
        String typeString = "sub"+ "(" + getVarName() + ", " + getPredicateVariable() +")";
        return typeString + getPredicates().map(Predicate::toString).collect(Collectors.joining(""));
    }
}
