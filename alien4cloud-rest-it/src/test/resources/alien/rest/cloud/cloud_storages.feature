Feature: Create cloud storages

  Background:
    Given I am authenticated with "ADMIN" role
    And I upload a plugin
    And I create a cloud with name "Mount doom cloud" and plugin id "alien4cloud-mock-paas-provider:1.0" and bean name "mock-paas-provider"
    And I enable the cloud "Mount doom cloud"
    And I upload the archive "tosca-normative-types"
    And I upload the archive "alien-base-types"
    And I upload the archive "alien-extended-storage-types"
    And There are these users in the system
      | sangoku |
    And I add a role "APPLICATIONS_MANAGER" to user "sangoku"
    And I add a role "CLOUD_DEPLOYER" to user "sangoku" on the resource type "CLOUD" named "Mount doom cloud"
    And I have already created a cloud image with name "Ubuntu Trusty", architecture "x86_64", type "linux", distribution "Ubuntu" and version "14.04.1"
    And I add the cloud image "Ubuntu Trusty" to the cloud "Mount doom cloud" and match it to paaS image "UBUNTU"
    And I add the flavor with name "medium", number of CPUs 4, disk size 64 and memory size 4096 to the cloud "Mount doom cloud" and match it to paaS flavor "3"
    And I add the storage with id "STORAGE1" and device "/etc/dev1" and size 1024 to the cloud "Mount doom cloud"
    And I match the storage with name "STORAGE1" of the cloud "Mount doom cloud" to the PaaS resource "alienSTORAGE2"
    And I add the storage with id "STORAGE2" and device "/etc/dev2" and size 2048 to the cloud "Mount doom cloud"
    And I match the storage with name "STORAGE2" of the cloud "Mount doom cloud" to the PaaS resource "alienSTORAGE2"
    And I am authenticated with user named "sangoku"
    And I have an application "ALIEN" with a topology containing a nodeTemplate "ConfigurableBlockStorage" related to "alien.nodes.ConfigurableBlockStorage:1.0-SNAPSHOT"
    And I add a node template "ConfigurableBlockStorage" related to the "alien.nodes.ConfigurableBlockStorage:1.0-SNAPSHOT" node type
    And I assign the cloud with name "Mount doom cloud" for the application

  Scenario: Match a topology for storage, no filter
    When I match for resources for my application on the cloud
    Then I should receive a match result with 2 storages for the node "ConfigurableBlockStorage":
      | STORAGE1 | /etc/dev1 | 1024 |
      | STORAGE2 | /etc/dev2 | 2048 |

  Scenario: Should be able to add and remove a storage
    When I match for resources for my application on the cloud
    Then I should receive a match result with 2 storages for the node "ConfigurableBlockStorage":
      | STORAGE1 | /etc/dev1 | 1024 |
      | STORAGE2 | /etc/dev2 | 2048 |
    And I am authenticated with "ADMIN" role
    And I add the storage with id "STORAGE3" and device "/etc/dev3" and size 2048 to the cloud "Mount doom cloud"
    And I match the storage with name "STORAGE3" of the cloud "Mount doom cloud" to the PaaS resource "alienSTORAGE2"
    And I match for resources for my application on the cloud
    Then I should receive a match result with 3 storages for the node "ConfigurableBlockStorage":
      | STORAGE1 | /etc/dev1 | 1024 |
      | STORAGE2 | /etc/dev2 | 2048 |
      | STORAGE3 | /etc/dev3 | 2048 |
    Then I remove the storage with name "STORAGE3" from the cloud "Mount doom cloud"
    When I match for resources for my application on the cloud
    Then I should receive a match result with 2 storages for the node "ConfigurableBlockStorage":
      | STORAGE1 | /etc/dev1 | 1024 |
      | STORAGE2 | /etc/dev2 | 2048 |