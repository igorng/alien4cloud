Feature: Location management

Background:
  Given I am authenticated with "ADMIN" role
  And I upload the archive "tosca-normative-types-1.0.0-SNAPSHOT"
  And I upload a plugin
  And I create an orchestrator named "Mount doom orchestrator" and plugin id "alien4cloud-mock-paas-provider:1.0" and bean name "mock-orchestrator-factory"
  And I enable the orchestrator "Mount doom orchestrator"
  
Scenario: Check the portability info are well injected when importing the orchestrator or location archive
  When I create a location named "Thark location" and infrastructure type "OpenStack" to the orchestrator "Mount doom orchestrator"
  Then I should receive a RestResponse with no error
  
  ## orchestrator archive
  When I try to get a component with id "alien.nodes.mock.PublicNetwork:1.0"
  Then I should receive a RestResponse with no error
  And the node type component should have tho folowing portability informations:
  	|orchestrators| Mock Orchestrator|  
  
  ## location archive
  When I try to get a component with id "alien.nodes.mock.openstack.Image:1.0"
  Then I should receive a RestResponse with no error
  And the node type component should have tho folowing portability informations:
  	|iaaSs| OpenStack|
  	|orchestrators| Mock Orchestrator|
  	
 ## check that the injected values are merged with the one defined at parsing
 When I try to get a component with id "alien.nodes.mock.openstack.Flavor:1.0"
   Then I should receive a RestResponse with no error
  And the node type component should have tho folowing portability informations:
  	|iaaSs| OpenStack|
  	|orchestrators|Mock Orchestrator,Mock|
  	
 
  	
 


