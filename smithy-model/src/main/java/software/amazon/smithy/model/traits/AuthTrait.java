/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.model.traits;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.ArrayNode;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.utils.ListUtils;
import software.amazon.smithy.utils.SetUtils;

/**
 * Specifies the auth schemes supported by default for operations
 * bound to a service.
 */
public final class AuthTrait extends AbstractTrait {

    public static final ShapeId ID = ShapeId.from("smithy.api#auth");

    private final Set<ShapeId> values;

    @Deprecated
    public AuthTrait(List<ShapeId> values, FromSourceLocation sourceLocation) {
        super(ID, sourceLocation);
        this.values = SetUtils.orderedCopyOf(values);
    }

    @Deprecated
    public AuthTrait(List<ShapeId> values) {
        this(values, SourceLocation.NONE);
    }

    public AuthTrait(Set<ShapeId> values, FromSourceLocation sourceLocation) {
        super(ID, sourceLocation);
        this.values = SetUtils.orderedCopyOf(values);
    }

    public AuthTrait(Set<ShapeId> values) {
        this(values, SourceLocation.NONE);
    }

    /**
     * Gets the auth scheme trait values.
     *
     * @return Returns the supported auth schemes.
     */
    public Set<ShapeId> getValueSet() {
        return values;
    }

    @Deprecated
    public List<ShapeId> getValues() {
        return ListUtils.copyOf(values);
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public Trait createTrait(ShapeId target, Node value) {
            Set<ShapeId> values = new LinkedHashSet<>();
            for (StringNode node : value.expectArrayNode().getElementsAs(StringNode.class)) {
                values.add(node.expectShapeId());
            }
            AuthTrait trait = new AuthTrait(values, value.getSourceLocation());
            trait.setNodeCache(value);
            return trait;
        }
    }

    @Override
    protected Node createNode() {
        return getValueSet().stream().map(ShapeId::toString).map(Node::from)
                .collect(ArrayNode.collect(getSourceLocation()));
    }
}
