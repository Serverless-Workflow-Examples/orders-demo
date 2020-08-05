$(document).ready(function() {
    var host = window.location.host;

    var workflowSocket = new WebSocket("ws://" + host + "/demo/processinstances");
    workflowSocket.onmessage = function (event) {
        addToWorkflowsTable(event.data);
    };

    var humandecisionsSocket = new WebSocket("ws://" + host + "/demo/usertasks");
    humandecisionsSocket.onmessage = function (event) {
        addToHumanDecisionsTable(event.data);
        var usertaskData = JSON.parse(event.data);
        if (usertaskData.kogitoUserTaskinstanceState == 'Ready') {
             addToManagerApprovalTable(event.data);
        }
    };

    var orderDecisionSocket = new WebSocket("ws://" + host + "/demo/finaldecisions");
    orderDecisionSocket.onmessage = function (event) {
        addToOrderDecisionsTable(event.data);
    };
});


var workflowsTable = $('#workflowinstancestable').DataTable({ searching: false, paging: false, info: false });
var counter = 1;

function addToWorkflowsTable(eventdata) {
     var eventobj = JSON.parse(eventdata);
     var processinstancestate = eventobj.kogitoProcessinstanceState
     var processinstancestateword = "";
     if (processinstancestate == 0) {
          processinstancestateword = "Pending";
     }
     if (processinstancestate == 1) {
          processinstancestateword = "Active";
     }
     if (processinstancestate == 2) {
          processinstancestateword = "Completed";
     }
     if (processinstancestate == 3) {
          processinstancestateword = "Aborted";
     }
     if (processinstancestate == 4) {
          processinstancestateword = "Suspended";
     }
     if (processinstancestate == 5) {
          processinstancestateword = "Error";
     }

     workflowsTable.row.add([
          counter,
          eventobj.data.processId,
          eventobj.data.processName,
          eventobj.kogitoProcessinstanceId,
          processinstancestateword
     ]).draw(false);

     counter++;
}

var humandecisionsTable = $('#humandecisioninstancestable').DataTable({ searching: false, paging: false, info: false });
var counterb = 1;

function addToHumanDecisionsTable(eventdata) {
     var eventobj = JSON.parse(eventdata);
     humandecisionsTable.row.add([
          counterb,
          eventobj.kogitoProcessId,
          eventobj.kogitoProcessinstanceId,
          eventobj.kogitoUserTaskinstanceId,
          eventobj.kogitoUserTaskinstanceState,
          eventobj.source
     ]).draw(false);

     counterb++;
}

$("#creteorder").on('click', function () {
     $.ajax({
          url: '/neworder',
          type: "POST",
          dataType: 'json',
          headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
          },
          data: JSON.stringify($("#newOrderForm").serializeToJSON()),
          success: function (result) {
               console.log(result);
          },
          error: function (xhr, resp, text) {
               console.log(xhr, resp, text);
          }
     })
});

var managerapprovalsTable = $('#managerapprovaltable').DataTable({
     searching: false, paging: false, info: false,
     "columnDefs": [{
          "targets": -1,
          "data": null,
          "defaultContent": "<button id=\"managerApproveButton\">Aprove</button><button id=\"managerDenyButton\">Deny</button>"
     }]
});
var counterc = 1;

function addToManagerApprovalTable(eventdata) {
     var eventobj = JSON.parse(eventdata);
     managerapprovalsTable.row.add([
          counterc,
          eventobj.kogitoProcessinstanceId,
          eventobj.kogitoUserTaskinstanceId,
          eventobj.data.inputs.workflowdata.cost
     ]).draw(false);

     counterc++;
}

var managerDecisionStr = "";

$('#managerapprovaltable tbody').on('click', '#managerApproveButton', function () {
     var data = managerapprovalsTable.row($(this).parents('tr')).data();
     doManagerApproval(data[1], data[2], 'Approved');
     managerapprovalsTable.clear().draw();
     managerDecisionStr = "Approved";
});

$('#managerapprovaltable tbody').on('click', '#managerDenyButton', function () {
     var data = managerapprovalsTable.row($(this).parents('tr')).data();
     doManagerApproval(data[1], data[2], 'Denied');
     managerapprovalsTable.clear().draw();
     managerDecisionStr = "Denied"
});

function doManagerApproval(processinstance, taskinstance, managerDecision) {
     var managerDecisionData = {"decision" : managerDecision};
     $.ajax({
          url: '/ordersworkflow/' + processinstance + '/managerApproval/' + taskinstance + '?user=managers&group=managers',
          type: "POST",
          dataType: 'json',
          headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
          },
          data: JSON.stringify(managerDecisionData),
          success: function (result) {
               console.log(result);
          },
          error: function (xhr, resp, text) {
               console.log(xhr, resp, text);
          }
     });
}

var orderdecisonsTable = $('#orderdecisionstable').DataTable({ searching: false, paging: false, info: false });
var counterd = 1;

function addToOrderDecisionsTable(eventdata) {
     var eventobj = JSON.parse(eventdata);
     var autoDecision = eventobj.data.decision;
     var orderdec = "";
     if(autoDecision == "NeedManagerApproval") {
          orderdec = managerDecisionStr;
     } else {
          orderdec = autoDecision;
     }

     orderdecisonsTable.row.add([
          counterd,
          eventobj.kogitoProcessinstanceId,
          orderdec
     ]).draw(false);

     counterd++;
}
