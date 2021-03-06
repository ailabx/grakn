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

package grakn.core.graql.internal.reasoner.cache;

import grakn.core.graql.answer.ConceptMap;
import grakn.core.graql.internal.reasoner.query.ReasonerQueryImpl;
import grakn.core.graql.internal.reasoner.unifier.UnifierType;
import java.util.Collection;

/**
 * 
 * @param <Q>
 * @param <R>
 */
abstract class SimpleQueryCacheBase<
        Q extends ReasonerQueryImpl,
        R extends Collection<ConceptMap>> extends QueryCacheBase<Q, R, Q, R> {

    @Override
    UnifierType unifierType() { return UnifierType.EXACT; }

    @Override Q queryToKey(Q query) { return query; }
    @Override Q keyToQuery(Q key) { return key; }

}