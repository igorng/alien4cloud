/* global UTILS */
'use strict';

angular.module('alienUiApp').factory('commonNodeRendererService', [
  function() {

    return {

      //----------------------
      // Services
      //----------------------
      tooltip: function(node, nodeTemplate, nodeType) {
        var tooltipContent = '<div>';

        tooltipContent += '<div>' + node.id;
        if (nodeType.abstract) {
          var icoSize = 16;
          tooltipContent += ' <img src="images/abstract_ico.png" height="' + icoSize + '" width="' + icoSize + '"></img>';
        }
        tooltipContent += '</div>';
        if (nodeTemplate.properties) {
          if (typeof nodeTemplate.properties.version === 'string') {
            tooltipContent += '<div>' + 'v' + nodeTemplate.properties.version + '</div>';
          }
        }
        tooltipContent += '</div>';
        return tooltipContent;
      },

      getDisplayId: function(node, simple) {
        var nodeTemplateNameSizeCut = 2;
        if (node.id.length >= UTILS.maxNodeNameDrawSize || simple) {
          nodeTemplateNameSizeCut = simple === true ? 9 : nodeTemplateNameSizeCut;
          return node.id.substring(0, UTILS.maxNodeNameDrawSize - nodeTemplateNameSizeCut) + '...';
        } else {
          return node.id;
        }
      },

      removeRuntimeCount: function(runtimeGroup, id) {
        var groupSelection = runtimeGroup.select('#' + id);
        if (!groupSelection.empty()) {
          groupSelection.remove();
        }
      },

      appendCount: function(runtimeGroup, nodeInstancesCount, deletedCount, rectOriginX, rectOriginY, x, y, width) {
        var fontSize = nodeInstancesCount >= 100 ? 'x-small' : 'small';
        var shiftLeftBigCount = nodeInstancesCount >= 100 ? 4 : 0;
        runtimeGroup.append('text').attr('text-anchor', 'start')
          .attr('x', rectOriginX + width - x - shiftLeftBigCount)
          .attr('y', rectOriginY + y)
          .attr('font-weight', 'bold')
          .attr('font-size', fontSize)
          .attr('fill', 'white')
          .text(function() {
            return nodeInstancesCount ? nodeInstancesCount - deletedCount : 0;
          });
      },

      getNumberOfInstanceByStatus: function(nodeInstances, instanceStatus, state) {
        var count = 0;
        for (var instanceId in nodeInstances) {
          if (nodeInstances.hasOwnProperty(instanceId)) {
            if (nodeInstances[instanceId].instanceStatus === instanceStatus || nodeInstances[instanceId].state === state) {
              count++;
            }
          }
        }
        return count;
      }
    }; // end returned service

  } // function
]);
