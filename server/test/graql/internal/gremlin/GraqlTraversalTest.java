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

package grakn.core.graql.internal.gremlin;

import grakn.core.graql.concept.ConceptId;
import grakn.core.graql.concept.Label;
import grakn.core.graql.concept.Role;
import grakn.core.graql.query.Graql;
import grakn.core.graql.query.pattern.Pattern;
import grakn.core.graql.query.pattern.Var;
import grakn.core.graql.query.pattern.VarPattern;
import grakn.core.graql.query.pattern.Conjunction;
import grakn.core.graql.query.pattern.VarPatternAdmin;
import grakn.core.graql.internal.gremlin.fragment.Fragment;
import grakn.core.graql.internal.gremlin.fragment.Fragments;
import grakn.core.graql.query.pattern.Patterns;
import grakn.core.graql.query.pattern.property.IdProperty;
import grakn.core.graql.query.pattern.property.SubProperty;
import grakn.core.server.session.TransactionImpl;
import grakn.core.common.util.CommonUtil;
import grakn.core.graql.internal.Schema;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static grakn.core.graql.query.Graql.and;
import static grakn.core.graql.query.Graql.gt;
import static grakn.core.graql.query.Graql.var;
import static grakn.core.graql.internal.gremlin.GraqlMatchers.feature;
import static grakn.core.graql.internal.gremlin.GraqlMatchers.satisfies;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.id;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.inIsa;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.inRelates;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.inSub;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.outIsa;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.outRelates;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.outSub;
import static grakn.core.graql.internal.gremlin.fragment.Fragments.value;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraqlTraversalTest {

    private static final Var a = Graql.var("a");
    private static final Var b = Graql.var("b");
    private static final Var c = Graql.var("c");
    private static final Var x = Graql.var("x");
    private static final Var y = Graql.var("y");
    private static final Var z = Graql.var("z");
    private static final Var xx = Graql.var("xx");
    private static final Fragment xId = id(null, x, ConceptId.of("Titanic"));
    private static final Fragment yId = id(null, y, ConceptId.of("movie"));
    private static final Fragment xIsaY = outIsa(null, x, y);
    private static final Fragment yTypeOfX = inIsa(null, y, x, true);

    private static final GraqlTraversal fastIsaTraversal = traversal(yId, yTypeOfX);
    private static TransactionImpl<?> tx;
    private final String ROLE_PLAYER_EDGE = Schema.EdgeLabel.ROLE_PLAYER.getLabel();

    @Before
    public void setUp() {
        tx = mock(TransactionImpl.class);
        Role wife = mock(Role.class);
        when(wife.label()).thenReturn(Label.of("wife"));
        when(wife.isRole()).thenReturn(true);
        when(tx.getSchemaConcept(Label.of("wife"))).thenReturn(wife);
        //TODO: mock the following in a proper way
//        Role wife = graph.putRole("wife");
//        graph.putRelationshipType("marriage").relates(wife);
    }

    @Test
    public void testComplexityIndexVsIsa() {
        GraqlTraversal indexTraversal = traversal(xId);
        assertFaster(indexTraversal, fastIsaTraversal);
    }

    @Test
    public void testComplexityFastIsaVsSlowIsa() {
        GraqlTraversal slowIsaTraversal = traversal(xIsaY, yId);
        assertFaster(fastIsaTraversal, slowIsaTraversal);
    }

    @Test
    public void testComplexityConnectedVsDisconnected() {
        GraqlTraversal connectedDoubleIsa = traversal(xIsaY, outIsa(null, y, z));
        GraqlTraversal disconnectedDoubleIsa = traversal(xIsaY, inIsa(null, z, y, true));
        assertFaster(connectedDoubleIsa, disconnectedDoubleIsa);
    }

    @Test
    public void testGloballyOptimalIsFasterThanLocallyOptimal() {
        GraqlTraversal locallyOptimalSpecificInstance = traversal(yId, yTypeOfX, xId);
        GraqlTraversal globallyOptimalSpecificInstance = traversal(xId, xIsaY, yId);
        assertFaster(globallyOptimalSpecificInstance, locallyOptimalSpecificInstance);
    }

    @Test
    public void testRelatesFasterFromRoleType() {
        GraqlTraversal relatesFromRelationType = traversal(yId, outRelates(null, y, x), xId);
        GraqlTraversal relatesFromRoleType = traversal(xId, inRelates(null, x, y), yId);
        assertFaster(relatesFromRoleType, relatesFromRelationType);
    }

    @Test
    public void testResourceWithTypeFasterFromType() {
        GraqlTraversal fromInstance =
                traversal(outIsa(null, x, xx), id(null, xx, ConceptId.of("_")), inRolePlayer(x, z), outRolePlayer(z, y));
        GraqlTraversal fromType =
                traversal(id(null, xx, ConceptId.of("_")), inIsa(null, xx, x, true), inRolePlayer(x, z), outRolePlayer(z, y));
        assertFaster(fromType, fromInstance);
    }

    @Ignore //TODO: No longer applicable. Think of a new test to replace this.
    @Test
    public void valueFilteringIsBetterThanANonFilteringOperation() {
        GraqlTraversal valueFilterFirst = traversal(value(null, x, gt(1)), inRolePlayer(x, b), outRolePlayer(b, y), outIsa(null, y, z));
        GraqlTraversal rolePlayerFirst = traversal(outIsa(null, y, z), inRolePlayer(y, b), outRolePlayer(b, x), value(null, x, gt(1)));

        assertFaster(valueFilterFirst, rolePlayerFirst);
    }

    @Test
    public void testAllTraversalsSimpleQuery() {
        IdProperty titanicId = IdProperty.of(ConceptId.of("Titanic"));
        IdProperty movieId = IdProperty.of(ConceptId.of("movie"));
        SubProperty subProperty = SubProperty.of(Patterns.varPattern(y, ImmutableSet.of(movieId)));

        VarPattern pattern = Patterns.varPattern(x, ImmutableSet.of(titanicId, subProperty));
        Set<GraqlTraversal> traversals = allGraqlTraversals(pattern).collect(toSet());

        assertEquals(12, traversals.size());

        Fragment xId = id(titanicId, x, ConceptId.of("Titanic"));
        Fragment yId = id(movieId, y, ConceptId.of("movie"));
        Fragment xSubY = outSub(subProperty, x, y, Fragments.TRAVERSE_ALL_SUB_EDGES);
        Fragment ySubX = inSub(subProperty, y, x, Fragments.TRAVERSE_ALL_SUB_EDGES);

        Set<GraqlTraversal> expected = ImmutableSet.of(
                traversal(xId, xSubY, yId),
                traversal(xId, ySubX, yId),
                traversal(xId, yId, xSubY),
                traversal(xId, yId, ySubX),
                traversal(xSubY, xId, yId),
                traversal(xSubY, yId, xId),
                traversal(ySubX, xId, yId),
                traversal(ySubX, yId, xId),
                traversal(yId, xId, xSubY),
                traversal(yId, xId, ySubX),
                traversal(yId, xSubY, xId),
                traversal(yId, ySubX, xId)
        );

        assertEquals(expected, traversals);
    }

    @Test
    public void testOptimalShortQuery() {
        assertNearlyOptimal(x.isa(y.id(ConceptId.of("movie"))));
    }

    @Test
    public void testOptimalBothId() {
        assertNearlyOptimal(x.id(ConceptId.of("Titanic")).isa(y.id(ConceptId.of("movie"))));
    }

    @Test
    public void testOptimalByValue() {
        assertNearlyOptimal(x.val("hello").isa(y.id(ConceptId.of("movie"))));
    }

    @Ignore // TODO: This is now super-slow
    @Test
    public void testOptimalAttachedResource() {
        assertNearlyOptimal(var()
                .rel(x.isa(y.id(ConceptId.of("movie"))))
                .rel(z.val("Titanic").isa(var("a").id(ConceptId.of("title")))));
    }

    @Ignore // TODO: This is now super-slow
    @Test
    public void makeSureTypeIsCheckedBeforeFollowingARolePlayer() {
        assertNearlyOptimal(and(
                x.id(ConceptId.of("xid")),
                var().rel(x).rel(y),
                y.isa(b.label("person")),
                var().rel(y).rel(z)
        ));
    }

    @Ignore("Need to build proper mocks")
    @Test
    public void whenPlanningSimpleUnaryRelation_ApplyRolePlayerOptimisation() {
        VarPattern rel = var("x").rel("y");

        GraqlTraversal graqlTraversal = semiOptimal(rel);

        // I know this is horrible, unfortunately I can't think of a better way...
        // The issue is that some things we want to inspect are not public, mainly:
        // 1. The variable name assigned to the casting
        // 2. The role-player fragment classes
        // Both of these things should not be made public if possible, so I see this regex as the lesser evil
        assertThat(graqlTraversal, anyOf(
                matches("\\{§x-\\[" + ROLE_PLAYER_EDGE + ":#.*]->§y}"),
                matches("\\{§y<-\\[" + ROLE_PLAYER_EDGE + ":#.*]-§x}")
        ));
    }

    @Ignore("Need to build proper mocks")
    @Test
    public void whenPlanningSimpleBinaryRelationQuery_ApplyRolePlayerOptimisation() {
        VarPattern rel = var("x").rel("y").rel("z");

        GraqlTraversal graqlTraversal = semiOptimal(rel);

        assertThat(graqlTraversal, anyOf(
                matches("\\{§x-\\[" + ROLE_PLAYER_EDGE + ":#.*]->§.* §x-\\[" + ROLE_PLAYER_EDGE + ":#.*]->§.* #.*\\[neq:#.*]}"),
                matches("\\{§.*<-\\[" + ROLE_PLAYER_EDGE + ":#.*]-§x-\\[" + ROLE_PLAYER_EDGE + ":#.*]->§.* #.*\\[neq:#.*]}")
        ));
    }

    @Ignore("Need to build proper mocks")
    @Test
    public void whenPlanningBinaryRelationQueryWithType_ApplyRolePlayerOptimisation() {
        VarPattern rel = var("x").rel("y").rel("z").isa("marriage");

        GraqlTraversal graqlTraversal = semiOptimal(rel);

        assertThat(graqlTraversal, anyOf(
                matches(".*§x-\\[" + ROLE_PLAYER_EDGE + ":#.* rels:marriage]->§.* §x-\\[" + ROLE_PLAYER_EDGE + ":#.* rels:marriage]->§.* #.*\\[neq:#.*].*"),
                matches(".*§.*<-\\[" + ROLE_PLAYER_EDGE + ":#.* rels:marriage]-§x-\\[" + ROLE_PLAYER_EDGE + ":#.* rels:marriage]->§.* #.*\\[neq:#.*].*")
        ));
    }

    @Ignore("Need to build proper mocks")
    @Test
    public void testRolePlayerOptimisationWithRoles() {
        VarPattern rel = var("x").rel("y").rel("wife", "z");

        GraqlTraversal graqlTraversal = semiOptimal(rel);

        assertThat(graqlTraversal, anyOf(
                matches(".*§x-\\[" + ROLE_PLAYER_EDGE + ":#.* roles:wife]->§.* §x-\\[" + ROLE_PLAYER_EDGE + ":#.*]->§.* #.*\\[neq:#.*].*"),
                matches(".*§.*<-\\[" + ROLE_PLAYER_EDGE + ":#.* roles:wife]-§x-\\[" + ROLE_PLAYER_EDGE + ":#.*]->§.* #.*\\[neq:#.*].*")
        ));
    }

    private static GraqlTraversal semiOptimal(Pattern pattern) {
        return GreedyTraversalPlan.createTraversal(pattern.admin(), tx);
    }

    private static GraqlTraversal traversal(Fragment... fragments) {
        return traversal(ImmutableList.copyOf(fragments));
    }

    @SafeVarargs
    private static GraqlTraversal traversal(ImmutableList<Fragment>... fragments) {
        ImmutableSet<ImmutableList<Fragment>> fragmentsSet = ImmutableSet.copyOf(fragments);
        return GraqlTraversal.create(fragmentsSet);
    }

    private static Stream<GraqlTraversal> allGraqlTraversals(Pattern pattern) {
        Collection<Conjunction<VarPatternAdmin>> patterns = pattern.admin().getDisjunctiveNormalForm().getPatterns();

        List<Set<List<Fragment>>> collect = patterns.stream()
                .map(conjunction -> new ConjunctionQuery(conjunction, tx))
                .map(ConjunctionQuery::allFragmentOrders)
                .collect(toList());

        Set<List<List<Fragment>>> lists = Sets.cartesianProduct(collect);

        return lists.stream()
                .map(Sets::newHashSet)
                .map(GraqlTraversalTest::createTraversal)
                .flatMap(CommonUtil::optionalToStream);
    }

    // Returns a traversal only if the fragment ordering is valid
    private static Optional<GraqlTraversal> createTraversal(Set<List<Fragment>> fragments) {

        // Make sure all dependencies are met
        for (List<Fragment> fragmentList : fragments) {
            Set<Var> visited = new HashSet<>();

            for (Fragment fragment : fragmentList) {
                if (!visited.containsAll(fragment.dependencies())) {
                    return Optional.empty();
                }

                visited.addAll(fragment.vars());
            }
        }

        return Optional.of(GraqlTraversal.create(fragments));
    }

    private static Fragment outRolePlayer(Var relation, Var rolePlayer) {
        return Fragments.outRolePlayer(null, relation, a, rolePlayer, null, null, null);
    }

    private static Fragment inRolePlayer(Var rolePlayer, Var relation) {
        return Fragments.inRolePlayer(null, rolePlayer, c, relation, null, null, null);
    }

    private static void assertNearlyOptimal(Pattern pattern) {
        GraqlTraversal traversal = semiOptimal(pattern);

        //noinspection OptionalGetWithoutIsPresent
        GraqlTraversal globalOptimum = allGraqlTraversals(pattern).min(comparing(GraqlTraversal::getComplexity)).get();

        double globalComplexity = globalOptimum.getComplexity();
        double complexity = traversal.getComplexity();

        assertTrue(
                "Expected\n " +
                        complexity + ":\t" + traversal + "\nto be similar speed to\n " +
                        globalComplexity + ":\t" + globalOptimum,
                complexity - globalComplexity <= 0.01
        );
    }

    private static void assertFaster(GraqlTraversal fast, GraqlTraversal slow) {
        double fastComplexity = fast.getComplexity();
        double slowComplexity = slow.getComplexity();
        boolean condition = fastComplexity < slowComplexity;

        assertTrue(
                "Expected\n" + fastComplexity + ":\t" + fast + "\nto be faster than\n" + slowComplexity + ":\t" + slow,
                condition
        );
    }

    private <T> Matcher<T> matches(String regex) {
        return feature(satisfies(string -> string.matches(regex)), "matching " + regex, Object::toString);
    }
}