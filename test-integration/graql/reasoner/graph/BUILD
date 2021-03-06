java_library(
    name = "diagonal-graph",
    srcs = ["DiagonalGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:diagonal-test"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "dual-linear-transitivity-graph",
    srcs = ["DualLinearTransitivityMatrixGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:dual-linear-transitivity"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "generic-schema-graph",
    srcs = ["GenericSchemaGraph.java"],
    data = ["//test-integration/graql/reasoner/resources:generic-schema"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//server",
        "//test-integration/graql/reasoner/pattern:query-pattern",
        "//test-integration/graql/reasoner/pattern:relation-pattern",
        "//test-integration/graql/reasoner/pattern:resource-pattern",
        "//test-integration/graql/reasoner/pattern:type-pattern",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "geo-graph",
    srcs = ["GeoGraph.java"],
    visibility = ["//test-integration:__subpackages__"],
    deps = ["//server"],
)

java_library(
    name = "linear-transitivity-matrix-graph",
    srcs = ["LinearTransitivityMatrixGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:linear-transivity"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "nguyen-graph",
    srcs = ["NguyenGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources/recursion:nguyen"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "path-matrix-graph",
    srcs = ["PathMatrixGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:path-test"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "path-tree-graph",
    srcs = ["PathTreeGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:path-test"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "path-tree-symmetric-graph",
    srcs = [
        "PathTreeGraph.java",
        "PathTreeSymmetricGraph.java",
    ],
    resources = ["//test-integration/graql/reasoner/resources:path-test-symmetric"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "tail-recursion-graph",
    srcs = ["TailRecursionGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources/recursion:tail-recursion"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//dependencies/maven/artifacts/com/google/guava",
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "transitivity-chain-graph",
    srcs = ["TransitivityChainGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:quadratic-transitivity"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)

java_library(
    name = "transitivity-matrix-graph",
    srcs = ["TransitivityMatrixGraph.java"],
    resources = ["//test-integration/graql/reasoner/resources:quadratic-transitivity"],
    visibility = ["//test-integration:__subpackages__"],
    deps = [
        "//server",
        "//test-integration/util:graql-test-util",
    ],
)
