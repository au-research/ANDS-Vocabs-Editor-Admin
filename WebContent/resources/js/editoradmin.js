/**
 * ANDS Editor Admin. Requires JQuery.
 */

/* Toggle all checkboxes of a given class. If all checkboxes are
 * already selected, then deselect all. Otherwise, select all. */
function toggleCheckboxes(elementStyle) {
  var allElements = jQuery("." + elementStyle);
  if (allElements.length == jQuery("." + elementStyle + ":checked").length) {
    allElements.prop("checked", false);
  } else {
    allElements.prop("checked", true);
  }
};

/* Toggle display of the text of a SPARQL Query/Update. */
function toggle_sparql(id) {
  var element = jQuery("." + id);
  element.toggle();
}
