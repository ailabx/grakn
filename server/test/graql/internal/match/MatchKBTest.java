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

package grakn.core.graql.internal.match;

import com.google.common.collect.Sets;
import grakn.core.graql.query.pattern.Patterns;
import grakn.core.server.Transaction;
import org.junit.Test;

import static grakn.core.graql.query.Graql.var;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.mock;

public class MatchKBTest {

    private final AbstractMatch query =
            new MatchBase(Patterns.conjunction(Sets.newHashSet(var("x").admin())));

    @Test
    public void matchesContainingTheSameGraphAndMatchBaseAreEqual() {
        Transaction graph = mock(Transaction.class);

        MatchTx query1 = new MatchTx(graph, query);
        MatchTx query2 = new MatchTx(graph, query);

        assertEquals(query1, query2);
        assertEquals(query1.hashCode(), query2.hashCode());
    }

    @Test
    public void matchesContainingDifferentGraphsAreNotEqual() {
        Transaction graph1 = mock(Transaction.class);
        Transaction graph2 = mock(Transaction.class);

        MatchTx query1 = new MatchTx(graph1, query);
        MatchTx query2 = new MatchTx(graph2, query);

        assertNotEquals(query1, query2);
    }
}