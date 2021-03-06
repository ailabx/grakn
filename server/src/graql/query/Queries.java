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

import grakn.core.graql.query.pattern.Var;
import grakn.core.graql.query.pattern.VarPatternAdmin;
import grakn.core.server.Transaction;
import grakn.core.graql.exception.GraqlQueryException;
import grakn.core.graql.admin.MatchAdmin;
import grakn.core.graql.answer.Answer;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

/**
 * Internal query factory
 *
 */
public class Queries {

    private Queries() {
    }

    public static GetQuery get(MatchAdmin match, ImmutableSet<Var> vars) {
        validateMatchVars(match, vars);
        return GetQuery.of(match, vars);
    }

    public static InsertQuery insert(Transaction tx, Collection<VarPatternAdmin> vars) {
        return InsertQuery.create(tx, null, vars);
    }

    /**
     * @param match the {@link Match} to insert for each result
     * @param varPattern  a collection of Vars to insert
     */
    public static InsertQuery insert(MatchAdmin match, Collection<VarPatternAdmin> varPattern) {
        return InsertQuery.create(match.tx(), match, varPattern);
    }

    public static DeleteQuery delete(MatchAdmin match, Set<Var> vars) {
        validateMatchVars(match, vars);
        return DeleteQuery.of(vars, match);
    }

    public static <T extends Answer> AggregateQuery<T> aggregate(MatchAdmin match, Aggregate<T> aggregate) {
        //TODO: validate vars in aggregate query
        return AggregateQuery.of(match, aggregate);
    }

    private static void validateMatchVars(MatchAdmin match, Set<Var> vars) {
        Set<Var> selectedVars = match.getSelectedNames();

        for (Var var : vars) {
            if (!selectedVars.contains(var)) {
                throw GraqlQueryException.varNotInQuery(var);
            }
        }
    }
}
