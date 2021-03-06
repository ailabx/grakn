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

import grakn.core.graql.answer.ConceptMap;
import grakn.core.graql.concept.Label;
import grakn.core.graql.concept.SchemaConcept;
import grakn.core.graql.query.pattern.Pattern;
import grakn.core.graql.query.pattern.PatternAdmin;
import grakn.core.graql.answer.Answer;
import grakn.core.graql.answer.AnswerGroup;
import grakn.core.graql.answer.Value;
import grakn.core.graql.parser.QueryParser;
import grakn.core.graql.query.pattern.Patterns;
import grakn.core.graql.query.aggregate.CountAggregate;
import grakn.core.graql.query.aggregate.GroupAggregate;
import grakn.core.graql.query.aggregate.ListAggregate;
import grakn.core.graql.query.aggregate.MaxAggregate;
import grakn.core.graql.query.aggregate.MeanAggregate;
import grakn.core.graql.query.aggregate.MedianAggregate;
import grakn.core.graql.query.aggregate.MinAggregate;
import grakn.core.graql.query.aggregate.StdAggregate;
import grakn.core.graql.query.aggregate.SumAggregate;
import grakn.core.graql.query.pattern.Var;
import grakn.core.graql.query.pattern.VarPattern;
import grakn.core.graql.query.predicate.Predicates;
import grakn.core.graql.internal.util.AdminConverter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import grakn.core.graql.query.predicate.ValuePredicate;

import javax.annotation.CheckReturnValue;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static grakn.core.graql.query.Syntax.Compute.Method;
import static java.util.stream.Collectors.toSet;

/**
 * Main class containing static methods for creating Graql queries.
 *
 * It is recommended you statically import these methods.
 *
 */
public class Graql {

    // QUERY BUILDING

    /**
     * @param patterns an array of patterns to match in the graph
     * @return a {@link Match} that will find matches of the given patterns
     */
    @CheckReturnValue
    public static Match match(Pattern... patterns) {
        return new QueryBuilder().match(patterns);
    }

    /**
     * @param patterns a collection of patterns to match in the graph
     * @return a {@link Match} that will find matches of the given patterns
     */
    @CheckReturnValue
    public static Match match(Collection<? extends Pattern> patterns) {
        return new QueryBuilder().match(patterns);
    }

    /**
     * @param varPatterns an array of variable patterns to insert into the graph
     * @return an insert query that will insert the given variable patterns into the graph
     */
    @CheckReturnValue
    public static InsertQuery insert(VarPattern... varPatterns) {
        return new QueryBuilder().insert(varPatterns);
    }

    /**
     * @param varPatterns a collection of variable patterns to insert into the graph
     * @return an insert query that will insert the given variable patterns into the graph
     */
    @CheckReturnValue
    public static InsertQuery insert(Collection<? extends VarPattern> varPatterns) {
        return new QueryBuilder().insert(varPatterns);
    }

    /**
     * @param varPatterns an array of {@link VarPattern}s defining {@link SchemaConcept}s
     * @return a {@link DefineQuery} that will apply the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static DefineQuery define(VarPattern... varPatterns) {
        return new QueryBuilder().define(varPatterns);
    }

    /**
     * @param varPatterns a collection of {@link VarPattern}s defining {@link SchemaConcept}s
     * @return a {@link DefineQuery} that will apply the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static DefineQuery define(Collection<? extends VarPattern> varPatterns) {
        return new QueryBuilder().define(varPatterns);
    }

    /**
     * @param varPatterns an array of {@link VarPattern}s undefining {@link SchemaConcept}s
     * @return a {@link UndefineQuery} that will remove the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static UndefineQuery undefine(VarPattern... varPatterns) {
        return new QueryBuilder().undefine(varPatterns);
    }

    /**
     * @param varPatterns a collection of {@link VarPattern}s undefining {@link SchemaConcept}s
     * @return a {@link UndefineQuery} that will remove the changes described in the {@code patterns}
     */
    @CheckReturnValue
    public static UndefineQuery undefine(Collection<? extends VarPattern> varPatterns) {
        return new QueryBuilder().undefine(varPatterns);
    }

    @CheckReturnValue
    public static <T extends Answer> ComputeQuery<T> compute(Method<T> method) {
        return new QueryBuilder().compute(method);
    }

    /**
     * Get a {@link QueryParser} for parsing queries from strings
     */
    public static QueryParser parser() {
        return new QueryBuilder().parser();
    }

    /**
     * @param queryString a string representing a query
     * @return a query, the type will depend on the type of query.
     */
    @CheckReturnValue
    public static <T extends Query<?>> T parse(String queryString) {
        return new QueryBuilder().parse(queryString);
    }

    // PATTERNS AND VARS

    /**
     * @param name the name of the variable
     * @return a new query variable
     */
    @CheckReturnValue
    public static Var var(String name) {
        return Patterns.var(name);
    }

    /**
     * @return a new, anonymous query variable
     */
    @CheckReturnValue
    public static Var var() {
        return Patterns.var();
    }

    /**
     * @param label the label of a concept
     * @return a variable pattern that identifies a concept by label
     */
    @CheckReturnValue
    public static VarPattern label(Label label) {
        return var().label(label);
    }

    /**
     * @param label the label of a concept
     * @return a variable pattern that identifies a concept by label
     */
    @CheckReturnValue
    public static VarPattern label(String label) {
        return var().label(label);
    }

    /**
     * @param patterns an array of patterns to match
     * @return a pattern that will match only when all contained patterns match
     */
    @CheckReturnValue
    public static Pattern and(Pattern... patterns) {
        return and(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match
     * @return a pattern that will match only when all contained patterns match
     */
    @CheckReturnValue
    public static Pattern and(Collection<? extends Pattern> patterns) {
        Collection<PatternAdmin> patternAdmins = AdminConverter.getPatternAdmins(patterns);
        return Patterns.conjunction(Sets.newHashSet(patternAdmins));
    }

    /**
     * @param patterns an array of patterns to match
     * @return a pattern that will match when any contained pattern matches
     */
    @CheckReturnValue
    public static Pattern or(Pattern... patterns) {
        return or(Arrays.asList(patterns));
    }

    /**
     * @param patterns a collection of patterns to match
     * @return a pattern that will match when any contained pattern matches
     */
    @CheckReturnValue
    public static Pattern or(Collection<? extends Pattern> patterns) {
        // Simplify representation when there is only one alternative
        if (patterns.size() == 1) {
            return Iterables.getOnlyElement(patterns);
        }

        Collection<PatternAdmin> patternAdmins = AdminConverter.getPatternAdmins(patterns);
        return Patterns.disjunction(Sets.newHashSet(patternAdmins));
    }


    // AGGREGATES

    /**
     * Create an aggregate that will count the results of a query.
     */
    @CheckReturnValue
    public static Aggregate<Value> count(String... vars) {
        return new CountAggregate(Arrays.stream(vars).map(Graql::var).collect(toSet()));
    }

    /**
     * Aggregate that counts results of a {@link Match}.
     */
    public static Aggregate<Value> count(Var var, Var... vars) {
        Set<Var> varSet = new HashSet<>(vars.length + 1);
        varSet.add(var);
        varSet.addAll(Arrays.asList(vars));
        return new CountAggregate(varSet);
    }

    /**
     * Create an aggregate that will sum the values of a variable.
     */
    @CheckReturnValue
    public static Aggregate<Value> sum(String var) {
        return new SumAggregate(Graql.var(var));
    }

    /**
     * Create an aggregate that will sum the values of a variable.
     */
    @CheckReturnValue
    public static Aggregate<Value> sum(Var var) {
        return new SumAggregate(var);
    }

    /**
     * Create an aggregate that will find the minimum of a variable's values.
     * @param var the variable to find the maximum of
     */
    @CheckReturnValue
    public static Aggregate<Value> min(String var) {
        return new MinAggregate(Graql.var(var));
    }

    /**
     * Create an aggregate that will find the minimum of a variable's values.
     * @param var the variable to find the maximum of
     */
    @CheckReturnValue
    public static Aggregate<Value> min(Var var) {
        return new MinAggregate(var);
    }

    /**
     * Create an aggregate that will find the maximum of a variable's values.
     * @param var the variable to find the maximum of
     */
    @CheckReturnValue
    public static Aggregate<Value> max(String var) {
        return new MaxAggregate(Graql.var(var));
    }

    /**
     * Create an aggregate that will find the maximum of a variable's values.
     * @param var the variable to find the maximum of
     */
    @CheckReturnValue
    public static Aggregate<Value> max(Var var) {
        return new MaxAggregate(var);
    }

    /**
     * Create an aggregate that will find the mean of a variable's values.
     * @param var the variable to find the mean of
     */
    @CheckReturnValue
    public static Aggregate<Value> mean(String var) {
        return new MeanAggregate(Graql.var(var));
    }

    /**
     * Create an aggregate that will find the mean of a variable's values.
     * @param var the variable to find the mean of
     */
    @CheckReturnValue
    public static Aggregate<Value> mean(Var var) {
        return new MeanAggregate(var);
    }

    /**
     * Create an aggregate that will find the median of a variable's values.
     * @param var the variable to find the median of
     */
    @CheckReturnValue
    public static Aggregate<Value> median(String var) {
        return new MedianAggregate(Graql.var(var));
    }

    /**
     * Create an aggregate that will find the median of a variable's values.
     * @param var the variable to find the median of
     */
    @CheckReturnValue
    public static Aggregate<Value> median(Var var) {
        return new MedianAggregate(var);
    }

    /**
     * Create an aggregate that will find the unbiased sample standard deviation of a variable's values.
     * @param var the variable to find the standard deviation of
     */
    @CheckReturnValue
    public static Aggregate<Value> std(String var) {
        return new StdAggregate(Graql.var(var));
    }

    /**
     * Create an aggregate that will find the unbiased sample standard deviation of a variable's values.
     * @param var the variable to find the standard deviation of
     */
    @CheckReturnValue
    public static Aggregate<Value> std(Var var) {
        return new StdAggregate(var);
    }

    /**
     * Create an aggregate that will group a query by a variable.
     * @param var the variable to group results by
     */
    @CheckReturnValue
    public static Aggregate<AnswerGroup<ConceptMap>> group(String var) {
        return group(var, new ListAggregate());
    }

    /**
     * Aggregate that groups results of a {@link Match} by variable name
     * @param varName the variable name to group results by
     */
    public static Aggregate<AnswerGroup<ConceptMap>> group(Var varName) {
        return group(varName, new ListAggregate());
    }

    /**
     * Create an aggregate that will group a query by a variable and apply the given aggregate to each group
     * @param var the variable to group results by
     * @param aggregate the aggregate to apply to each group
     * @param <T> the type the aggregate returns
     */
    @CheckReturnValue
    public static <T extends Answer> Aggregate<AnswerGroup<T>> group(String var, Aggregate<T> aggregate) {
        return group(Graql.var(var), aggregate);
    }

    /**
     * Aggregate that groups results of a {@link Match} by variable name, applying an aggregate to each group.
     * @param <T> the type of each group
     */
    public static <T extends Answer> Aggregate<AnswerGroup<T>> group(Var varName, Aggregate<T> innerAggregate) {
        return new GroupAggregate<>(varName, innerAggregate);
    }


    // PREDICATES

    /**
     * @param value the value
     * @return a predicate that is true when a value equals the specified value
     */
    @CheckReturnValue
    public static ValuePredicate eq(Object value) {
        Objects.requireNonNull(value);
        return Predicates.eq(value);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that is true when a value equals the specified value
     */
    @CheckReturnValue
    public static ValuePredicate eq(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.eq(varPattern);
    }

    /**
     * @param value the value
     * @return a predicate that is true when a value does not equal the specified value
     */
    @CheckReturnValue
    public static ValuePredicate neq(Object value) {
        Objects.requireNonNull(value);
        return Predicates.neq(value);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that is true when a value does not equal the specified value
     */
    @CheckReturnValue
    public static ValuePredicate neq(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.neq(varPattern);
    }

    /**
     * @param value the value
     * @return a predicate that is true when a value is strictly greater than the specified value
     */
    @CheckReturnValue
    public static ValuePredicate gt(Comparable value) {
        Objects.requireNonNull(value);
        return Predicates.gt(value);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that is true when a value is strictly greater than the specified value
     */
    @CheckReturnValue
    public static ValuePredicate gt(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.gt(varPattern);
    }

    /**
     * @param value the value
     * @return a predicate that is true when a value is greater or equal to the specified value
     */
    @CheckReturnValue
    public static ValuePredicate gte(Comparable value) {
        Objects.requireNonNull(value);
        return Predicates.gte(value);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that is true when a value is greater or equal to the specified value
     */
    @CheckReturnValue
    public static ValuePredicate gte(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.gte(varPattern);
    }

    /**
     * @param value the value
     * @return a predicate that is true when a value is strictly less than the specified value
     */
    @CheckReturnValue
    public static ValuePredicate lt(Comparable value) {
        Objects.requireNonNull(value);
        return Predicates.lt(value);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that is true when a value is strictly less than the specified value
     */
    @CheckReturnValue
    public static ValuePredicate lt(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.lt(varPattern);
    }

    /**
     * @param value the value
     * @return a predicate that is true when a value is less or equal to the specified value
     */
    @CheckReturnValue
    public static ValuePredicate lte(Comparable value) {
        Objects.requireNonNull(value);
        return Predicates.lte(value);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that is true when a value is less or equal to the specified value
     */
    @CheckReturnValue
    public static ValuePredicate lte(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.lte(varPattern);
    }

    /**
     * @param pattern a regex pattern
     * @return a predicate that returns true when a value matches the given regular expression
     */
    @CheckReturnValue
    public static ValuePredicate regex(String pattern) {
        Objects.requireNonNull(pattern);
        return Predicates.regex(pattern);
    }

    /**
     * @param substring a substring to match
     * @return a predicate that returns true when a value contains the given substring
     */
    @CheckReturnValue
    public static ValuePredicate contains(String substring) {
        Objects.requireNonNull(substring);
        return Predicates.contains(substring);
    }

    /**
     * @param varPattern the variable pattern representing a resource
     * @return a predicate that returns true when a value contains the given substring
     */
    @CheckReturnValue
    public static ValuePredicate contains(VarPattern varPattern) {
        Objects.requireNonNull(varPattern);
        return Predicates.contains(varPattern.admin());
    }
}
