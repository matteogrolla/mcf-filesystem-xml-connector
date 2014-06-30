(function(){

    /**
     * Widget to select one or more root path to be crawled
     * for each root path filter are specified to include/exclude files or directories matching a certain expression.
     * The specs collected by the widget are stored in a hidden field as a json structure like this
     *
     * [
     {
         "path": "/fdfsds",
         "convertPathToUri": false,
         "filters": [
             {
                 "action": "include",
                 "object": "file",
                 "match": "*"
             },
             {
                 "action": "include",
                 "object": "folder",
                 "match": "*"
             }
         ]
     }
     ]
     * @param id
     * @constructor
     */

    function FileSelector(id, isEditable){
        this.id = id;
        this.data = [];
        this.isEditable = isEditable;

        this.widget = $("#"+id);
        this.rootPathTemplate = $("#"+this.id+"_rootPathTemplate");
        this.rootPathContainer = $("#"+this.id+"_rootPathContainer")
        this.filterTemplate = $("#"+this.id+"_filterTemplate");
        this.filterContainer = $("#"+this.id+"_filterContainer")
        this.valueHolder = $("#"+this.id+"_value");
        this.pathInput = $("#"+this.id+"_pathValue");

        if (!this.isEditable){
            this.hideEditControls();
        }

        //event handlers
        var me = this;
        $("#"+id+"_addPath").click(function(e){
            var pathSpec = {
                "path": me.pathInput.val(),
                "convertPathToUri": $("#"+this.id+"_convertPathToUri").is(":checked"),
                "filters": []
            };
            $.proxy(me.addPath, me)(pathSpec);
        });
        $("#"+id).on("click", ".deletePath", function(e){
            var clickedRootPath = $(e.target).closest('tr')[0];
            $.proxy(me.deletePath, me)(clickedRootPath);
        });
        $("#"+id).on("click", ".addFilter", function(e){
            var rootPathIndex = $.proxy(me._findRootPathIndex, me)(this);
            var filterWidget = $(this).closest(".filterWidget");
            var filterContainer = filterWidget.find(".filterContainer");
            var filterValues = {
                action: filterWidget.find("tfoot .action").val(),
                object: filterWidget.find("tfoot .object").val(),
                match: filterWidget.find("tfoot .match").val()
            };
            $.proxy(me.addFilter, me)(rootPathIndex, filterContainer, filterValues);
        });
        $("#"+id).on("click", ".deleteFilter", function(e){
            var rootPathIndex = $.proxy(me._findRootPathIndex, me)(this);
            var filterContainer = $(this).closest(".filterContainer");
            var filterRow = $(this).closest(".filterRow");
            $.proxy(me.deleteFilter, me)(rootPathIndex, filterContainer, filterRow);
        });

        this.loadValues();
    }

    function createNamespace(ns){
        tokens = ns.split(".");

        var currNs = window;
        for (i=0; i<tokens.length; i++){
            var currToken = tokens[i];

            if (currNs[currToken] == null){
                currNs[currToken] = {};
            }
            currNs = currNs[currToken];
        }
    }

    createNamespace("mcf.widgets");
    window.mcf.widgets.FileSelector = FileSelector;

    FileSelector.prototype._findRootPathIndex = function(clickedEl){
        var rootPath = $(clickedEl).closest(".rootPath");
        var rootPathIndex = this._findElementIndexInContainer(this.rootPathContainer, ".rootPath", rootPath);
        if (window.console){
            console.log('rootPathIndex: '+rootPathIndex);
        }
        return rootPathIndex;
    }

    FileSelector.prototype.hideEditControls = function(){
        $(".editMode").attr(
            "style", "display:none"
        )
    }

    FileSelector.prototype.loadValues = function(){
        var pathSpecs = JSON.parse(this.valueHolder.val());
        this.valueHolder.val("");

        for (var i=0; i<pathSpecs.length; i++){
            var pathSpec = pathSpecs[i];
            this.addPath(pathSpec);
        }
    }

    FileSelector.prototype.addPath = function(pathSpec){
        var rootPath = this.rootPathTemplate.clone().appendTo(this.rootPathContainer);
        rootPath.attr({
            "style": "display:span",
            "id": null
        });
        rootPath.find(".pathValue").eq(0).text(pathSpec.path);
        rootPath.find(".convertPathToUri").eq(0).text(pathSpec.convertPathToUri);

        var pathSpecWithoutFilters = {};
        $.extend(pathSpecWithoutFilters, pathSpec); //clone the object
        pathSpecWithoutFilters.filters = [];
        this.data.push(pathSpecWithoutFilters);

        var rootPathIndex = this._findRootPathIndex(rootPath);
        var filterContainer = rootPath.find(".filterContainer").eq(0);
        var defaultFilterSpecs = [
            {
                action: "include",
                object: "file",
                match: "*"
            },
            {
                action: "include",
                object: "directory",
                match: "*"
            }
        ];
        var filterSpecs = defaultFilterSpecs;
        if (pathSpec.filters.length !=0){
            filterSpecs = pathSpec.filters;
        }

        for (var i=0; i<filterSpecs.length; i++){
            var filterSpec = filterSpecs[i];
            this.addFilter(rootPathIndex, filterContainer, filterSpec);
        }

        if (window.console){
            console.log('[FileSelector:addPath]pathSpec: '+pathSpec);
        }
        this._updateValue();
    }

    FileSelector.prototype._updateValue = function(){
        this.valueHolder.val(JSON.stringify(this.data));
    }

    FileSelector.prototype.deletePath = function(clickedRootPath){
        var rootPathIndex = this._findElementIndexInContainer(this.rootPathContainer, ".rootPath", clickedRootPath);
        if (window.console){
            console.log('[FileSelector:deletePath]rootPathIndex: '+rootPathIndex);
        }

        clickedRootPath.remove();

        this.data.splice(rootPathIndex,1);
        this._updateValue();
    }

    /*
     N.B. element should be a single valued array (as the result of a jQuery selection)
     */
    FileSelector.prototype._findElementIndexInContainer = function(container, elementSelector, element){
        var elementList = container.find(elementSelector);
        for(i=0; i<elementList.length; i++){
            if (element[0] == elementList[i]){
                return i;
            }
        }
        return -1;
    }

    FileSelector.prototype.addFilter = function(rootPathIndex, filterContainer, values){

        var filter = this.filterTemplate.clone().appendTo(filterContainer);
        filter.attr({
            "style": "display:span",
            "id": null
        });

        filter.find(".action").text(values.action);
        filter.find(".object").text(values.object);
        filter.find(".match").text(values.match);

        var i=0;
        //TODO implement

        if (window.console){
            console.log(
                '[FileSelector:addFilter]values: '+values
            );
        }

        this.data[rootPathIndex].filters.push(values);
        this._updateValue();
    }

    FileSelector.prototype.deleteFilter = function(rootPathIndex, filterContainer, clickedFilterRow){
        var filterIndex = this._findElementIndexInContainer(filterContainer, ".filterRow", clickedFilterRow);
        if (window.console){
            console.log('[FileSelector:deleteFilter]filterIndex: '+filterIndex);
        }
        clickedFilterRow.remove();

        this.data[rootPathIndex].filters.splice(filterIndex,1);
        this._updateValue();
    }
})();



