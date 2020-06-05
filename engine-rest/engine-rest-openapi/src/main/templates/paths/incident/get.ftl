{

  <@lib.endpointInfo
      id = "getIncidents"
      tag = "Incident"
      desc = "Queries for incidents that fulfill given parameters. The size of the result set can be retrieved by using
              the [Get Incident Count](${docsUrl}/reference/rest/incident/get-query-count/) method." />

  "parameters": [
    <#assign last = true >
    <#include "/lib/commons/incident-query-params.ftl">
  ],
  "responses" : {

    <@lib.response
        code = "200"
        dto = "IncidentDto"
        desc = "Request successful."
        examples = ['"example-1": {
                       "summary": "GET `/incident/anIncidentId`",
                       "value": {
                           "id": "anIncidentId",
                           "processDefinitionId": "aProcDefId",
                           "processInstanceId": "aProcInstId",
                           "executionId": "anExecutionId",
                           "incidentTimestamp": "2014-03-01T08:00:00.000+0200",
                           "incidentType": "failedJob",
                           "activityId": "serviceTask",
                           "failedActivityId": "serviceTask",
                           "causeIncidentId": "aCauseIncidentId",
                           "rootCauseIncidentId": "aRootCauseIncidentId",
                           "configuration": "aConfiguration",
                           "tenantId": null,
                           "incidentMessage": "anIncidentMessage",
                           "jobDefinitionId": "aJobDefinitionId"
                         }
                     }'] />

    <@lib.response
        code = "404"
        dto = "ExceptionDto"
        last = true
        desc = "Returned if an incident with given id does not exist." />
    }
}