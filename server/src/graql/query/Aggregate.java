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

package grakn.core.graql.query;

import grakn.core.graql.answer.Answer;
import grakn.core.graql.answer.ConceptMap;

import javax.annotation.CheckReturnValue;
import java.util.List;
import java.util.stream.Stream;

/**
 * An aggregate operation to perform on a query.
 * @param <T> the type of the result of the aggregate operation
 *
 */
public interface Aggregate<T extends Answer> {
    /**
     * The function to apply to the stream of results to produce the aggregate result.
     * @param stream a stream of query results
     * @return the result of the aggregate operation
     */
    @CheckReturnValue
    List<T> apply(Stream<? extends ConceptMap> stream);

}
