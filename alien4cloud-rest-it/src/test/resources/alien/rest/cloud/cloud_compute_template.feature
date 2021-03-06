Feature: Create cloud compute templates

  Background:
    Given I am authenticated with "ADMIN" role
    And I upload a plugin
    And I create a cloud with name "Mount doom cloud" and plugin id "alien4cloud-mock-paas-provider:1.0" and bean name "mock-paas-provider"
    And I have already created a cloud image with name "Windows 7", architecture "x86_64", type "windows", distribution "Windows" and version "7.0"
    And I have already created a cloud image with name "Ubuntu Trusty", architecture "x86_64", type "linux", distribution "Ubuntu" and version "14.04.1"
    And I enable "Mount doom cloud"

  Scenario: Add cloud image, flavor to a cloud
    When I add the cloud image "Windows 7" to the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no error
    When I match the image "Windows 7" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS"
    Then I should receive a RestResponse with no compute templates
    When I add the flavor with name "small", number of CPUs 2, disk size 32 and memory size 2048 to the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no error
    When I match the flavor "small" of the cloud "Mount doom cloud" to the PaaS resource "2"
    Then I should receive a RestResponse with 1 compute templates:
      | Windows 7 | small | enabled |
    When I add the flavor with name "medium", number of CPUs 4, disk size 64 and memory size 4096 to the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no error
    When I match the flavor "medium" of the cloud "Mount doom cloud" to the PaaS resource "3"
    Then I should receive a RestResponse with 2 compute templates:
      | Windows 7 | small  | enabled |
      | Windows 7 | medium | enabled |
    When I add the cloud image "Ubuntu Trusty" to the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no error
    When I match the image "Ubuntu Trusty" of the cloud "Mount doom cloud" to the PaaS resource "UBUNTU"
    Then I should receive a RestResponse with 4 compute templates:
      | Windows 7     | small  | enabled |
      | Windows 7     | medium | enabled |
      | Ubuntu Trusty | small  | enabled |
      | Ubuntu Trusty | medium | enabled |
    And The cloud with name "Mount doom cloud" should have 4 compute templates as resources:
      | Windows 7     | small  | enabled |
      | Windows 7     | medium | enabled |
      | Ubuntu Trusty | small  | enabled |
      | Ubuntu Trusty | medium | enabled |

  Scenario: Add cloud image, flavor with enable disable template
    Given I add the cloud image "Windows 7" to the cloud "Mount doom cloud"
    And I add the cloud image "Ubuntu Trusty" to the cloud "Mount doom cloud"
    And I add the flavor with name "small", number of CPUs 2, disk size 32 and memory size 2048 to the cloud "Mount doom cloud"
    And I add the flavor with name "medium", number of CPUs 4, disk size 64 and memory size 4096 to the cloud "Mount doom cloud"
    And I match the image "Windows 7" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS"
    And I match the image "Ubuntu Trusty" of the cloud "Mount doom cloud" to the PaaS resource "UBUNTU"
    And I match the flavor "small" of the cloud "Mount doom cloud" to the PaaS resource "2"
    And I match the flavor "medium" of the cloud "Mount doom cloud" to the PaaS resource "3"
    When I disable the compute template of the cloud "Mount doom cloud" constituted of image "Windows 7" and flavor "small"
    Then The cloud with name "Mount doom cloud" should have 4 compute templates as resources:
      | Windows 7     | small  | not enabled |
      | Windows 7     | medium | enabled     |
      | Ubuntu Trusty | small  | enabled     |
      | Ubuntu Trusty | medium | enabled     |
    When I enable the compute template of the cloud "Mount doom cloud" constituted of image "Windows 7" and flavor "small"
    Then The cloud with name "Mount doom cloud" should have 4 compute templates as resources:
      | Windows 7     | small  | enabled |
      | Windows 7     | medium | enabled |
      | Ubuntu Trusty | small  | enabled |
      | Ubuntu Trusty | medium | enabled |

  Scenario: Remove cloud image, flavor from a cloud
    Given I add the cloud image "Windows 7" to the cloud "Mount doom cloud"
    And I add the cloud image "Ubuntu Trusty" to the cloud "Mount doom cloud"
    And I add the flavor with name "small", number of CPUs 2, disk size 32 and memory size 2048 to the cloud "Mount doom cloud"
    And I add the flavor with name "medium", number of CPUs 4, disk size 64 and memory size 4096 to the cloud "Mount doom cloud"
    And I match the image "Windows 7" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS"
    And I match the image "Ubuntu Trusty" of the cloud "Mount doom cloud" to the PaaS resource "UBUNTU"
    And I match the flavor "small" of the cloud "Mount doom cloud" to the PaaS resource "2"
    And I match the flavor "medium" of the cloud "Mount doom cloud" to the PaaS resource "3"
    When I remove the cloud image "Ubuntu Trusty" from the cloud "Mount doom cloud"
    Then I should receive a RestResponse with 2 compute templates:
      | Windows 7 | small  | enabled |
      | Windows 7 | medium | enabled |
    When I remove the flavor "small" from the cloud "Mount doom cloud"
    Then I should receive a RestResponse with 1 compute templates:
      | Windows 7 | medium | enabled |
    When I remove the cloud image "Windows 7" from the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no compute templates
    When I remove the flavor "medium" from the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no compute templates
    And The cloud with name "Mount doom cloud" should not have compute templates as resources

  Scenario: Compute template generation should take into account image requirement
    Given I have already created a cloud image with name "Windows 7 with requirement", architecture "x86_64", type "windows", distribution "Windows", version "7.0", min CPUs 4, min memory 2048, min disk 32
    And I add the cloud image "Windows 7 with requirement" to the cloud "Mount doom cloud"
    And I match the image "Windows 7 with requirement" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS"
    When I add the flavor with name "small_less_cpu", number of CPUs 2, disk size 32 and memory size 2048 to the cloud "Mount doom cloud"
    And I match the flavor "small_less_cpu" of the cloud "Mount doom cloud" to the PaaS resource "paas_too_small_cpu"
    Then I should receive a RestResponse with no compute templates
    When I add the flavor with name "small_less_disk_size", number of CPUs 4, disk size 31 and memory size 2048 to the cloud "Mount doom cloud"
    And I match the flavor "small_less_disk_size" of the cloud "Mount doom cloud" to the PaaS resource "paas_too_small_disk"
    Then I should receive a RestResponse with no compute templates
    When I add the flavor with name "small_less_memory_size", number of CPUs 4, disk size 32 and memory size 2047 to the cloud "Mount doom cloud"
    And I match the flavor "small_less_memory_size" of the cloud "Mount doom cloud" to the PaaS resource "paas_too_small_memory"
    Then I should receive a RestResponse with no compute templates
    When I add the flavor with name "medium", number of CPUs 4, disk size 64 and memory size 4096 to the cloud "Mount doom cloud"
    And I match the flavor "medium" of the cloud "Mount doom cloud" to the PaaS resource "paas_medium"
    Then I should receive a RestResponse with 1 compute templates:
      | Windows 7 with requirement | medium | enabled |
    Given I have already created a cloud image with name "Windows 7 with less requirement", architecture "x86_64", type "windows", distribution "Windows", version "7.0", min CPUs 1, min memory 1024, min disk 16
    And I add the cloud image "Windows 7 with less requirement" to the cloud "Mount doom cloud"
    And I match the image "Windows 7 with less requirement" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS_LESS_REQUIREMENT"
    Then I should receive a RestResponse with 5 compute templates:
      | Windows 7 with requirement      | medium                 | enabled |
      | Windows 7 with less requirement | medium                 | enabled |
      | Windows 7 with less requirement | small_less_cpu         | enabled |
      | Windows 7 with less requirement | small_less_disk_size   | enabled |
      | Windows 7 with less requirement | small_less_memory_size | enabled |

  Scenario: Match resource for compute template
    Given I add the cloud image "Windows 7" to the cloud "Mount doom cloud"
    And I add the flavor with name "small", number of CPUs 2, disk size 32 and memory size 2048 to the cloud "Mount doom cloud"
    And I match the image "Windows 7" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS"
    And I match the flavor "small" of the cloud "Mount doom cloud" to the PaaS resource "2"
    And The cloud "Mount doom cloud" should have resources mapping configuration as below:
      | Windows 7 | small | WINDOWS | 2 |
    Given I add the flavor with name "medium", number of CPUs 4, disk size 64 and memory size 4096 to the cloud "Mount doom cloud"
    And I match the flavor "medium" of the cloud "Mount doom cloud" to the PaaS resource "3"
    Then I should receive a RestResponse with no error
    And The cloud "Mount doom cloud" should have resources mapping configuration as below:
      | Windows 7 | small  | WINDOWS | 2 |
      | Windows 7 | medium | WINDOWS | 3 |
    When I delete the mapping for the image "Windows 7" of the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no error
    And The cloud "Mount doom cloud" should have empty resources mapping configuration
    When I match the image "Windows 7" of the cloud "Mount doom cloud" to the PaaS resource "WINDOWS"
    And I delete the mapping for the flavor "small" of the cloud "Mount doom cloud"
    Then I should receive a RestResponse with no error
    And The cloud "Mount doom cloud" should have resources mapping configuration as below:
      | Windows 7 | medium | WINDOWS | 3 |
    When I delete the mapping for the flavor "medium" of the cloud "Mount doom cloud"
    Then The cloud "Mount doom cloud" should have empty resources mapping configuration