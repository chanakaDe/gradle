/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.integtests

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.internal.hash.HashUtil
import org.junit.Rule

class WrapperChecksumVerificationTest extends AbstractIntegrationSpec {
    @Rule BlockingDownloadHttpServer server = new BlockingDownloadHttpServer(distribution.binDistribution)

    def setup() {
        executer.beforeExecute(new WrapperSetup())
    }

    def "wrapper execution fails when using bad checksum"() {
        given:
        buildFile << """
    wrapper {
        distributionUrl = '${server.distUri}'
    }
"""

        succeeds('wrapper')

        and:
        file('gradle/wrapper/gradle-wrapper.properties') << 'distributionSha256Sum=bad'

        when:
        def failure = executer.usingExecutable("gradlew").withStackTraceChecksDisabled().runWithFailure()

        then:
        failure.error.contains('hash sum comparison failed')
    }

    def "wrapper successfully verifies good checksum"() {
        given:
        buildFile << """
    wrapper {
        distributionUrl = '${server.distUri}'
    }
"""

        succeeds('wrapper')

        and:
        file('gradle/wrapper/gradle-wrapper.properties') << "distributionSha256Sum=${HashUtil.sha256(server.binZip).asHexString()}"

        when:
        def success = executer.usingExecutable("gradlew").run()

        then:
        success.output.contains('BUILD SUCCESSFUL')
    }
}
