/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.plugins

import org.gradle.api.plugins.PluginAware
import org.gradle.model.RuleSource
import spock.lang.Specification

class DefaultAppliedPluginsContainerTest extends Specification {

    PluginAware target = Mock(PluginAware) {
        toString() >> "PluginAwareMock"
    }

    DefaultAppliedPluginsContainer container = new DefaultAppliedPluginsContainer(target, null)

    @RuleSource
    static class TestRuleSource {}

    def "does not support rule applying source classes"() {
        when:
        container.apply(TestRuleSource)

        then:
        UnsupportedOperationException e = thrown()
        e.message == "Cannot apply model rules of plugin '${TestRuleSource.name}' as the target 'PluginAwareMock' is not model rule aware"
    }
}