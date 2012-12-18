/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tests

import spock.lang.Specification

/**
 * User: dwoods
 * Date: 11/26/12
 */
class ConfigObjectTests extends Specification {
  def 'check default source precedence in merge'() {
    def config1 = """
                  config {
                    a = 1
                    b = 2
                    c = 3
                  }
    """

    def config2 = """
                  config {
                    a = 2
                    b = 2
                  }
    """

    when:
      def configObject1 = new ConfigSlurper().parse( config1 )
      def configObject2 = new ConfigSlurper().parse( config2 )
      def merge = configObject2.merge( configObject1 )

    and:
      def configObject3 = new ConfigSlurper().parse( config1 )
      def configObject4 = new ConfigSlurper().parse( config2 )
      def mergeWithSourcePrecedence = configObject4.merge( configObject3, true )

    then:
      // overwrote config2's value
      assert merge.config.a == 1
      // preserved config2's value
      assert mergeWithSourcePrecedence.config.a == 2

      // config2 inherited value from config1
      assert merge.config.c == 3
      // config2 inherited from config1
      assert mergeWithSourcePrecedence.config.c == merge.config.c
  }

  def 'check recursion with source precedence in merge'() {
    def c = """
            base {
                name = "base"
                database {
                    username = "sa"
                    password = ""
                    hbm2ddl = "create-drop"
                    bootstrap_data = false
                }
            }
            dev {
                name = "dev"
                extendsFrom = "base"
                database {
                    hbm2ddl = "create"
                    bootstrap_data = false
                }
            }
    """

    when:
      // consume the config and recurse w/ no source precedence
      def noSourcePrecedenceConfigMap = new ConfigSlurper().parse( c )
      def noSourcePrecedenceEnvConfig = inheritConfigs( noSourcePrecedenceConfigMap, noSourcePrecedenceConfigMap["dev"], false )

    and:
      // "" with source precedence
      def withSourcePrecedenceConfigMap = new ConfigSlurper().parse( c )
      def withSourcePrecedenceEnvConfig = inheritConfigs( withSourcePrecedenceConfigMap, withSourcePrecedenceConfigMap["dev"], true )

    then:
      assert noSourcePrecedenceEnvConfig.name == "base"
      assert withSourcePrecedenceEnvConfig.name == "dev"
  }

  private static ConfigObject inheritConfigs( rootConfig, config, sourcePrecedence ) {
    def extConfig = rootConfig[ config?.extendsFrom ]
    if (extConfig) {
      config.merge( inheritConfigs( rootConfig, extConfig, sourcePrecedence ), sourcePrecedence )
    }
    return config
  }
}
