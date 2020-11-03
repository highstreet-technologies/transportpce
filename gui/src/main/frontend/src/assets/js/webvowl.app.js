webvowl.app =
/******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;
/******/
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ({

/***/ 0:
/***/ (function(module, exports, __webpack_require__) {

	__webpack_require__(321);
	__webpack_require__(323);
	
	module.exports = __webpack_require__(324);


/***/ }),

/***/ 6:
/***/ (function(module, exports) {

	module.exports = d3;

/***/ }),

/***/ 93:
/***/ (function(module, exports, __webpack_require__) {

	var Symbol = __webpack_require__(94),
	    getRawTag = __webpack_require__(97),
	    objectToString = __webpack_require__(98);
	
	/** `Object#toString` result references. */
	var nullTag = '[object Null]',
	    undefinedTag = '[object Undefined]';
	
	/** Built-in value references. */
	var symToStringTag = Symbol ? Symbol.toStringTag : undefined;
	
	/**
	 * The base implementation of `getTag` without fallbacks for buggy environments.
	 *
	 * @private
	 * @param {*} value The value to query.
	 * @returns {string} Returns the `toStringTag`.
	 */
	function baseGetTag(value) {
	  if (value == null) {
	    return value === undefined ? undefinedTag : nullTag;
	  }
	  return (symToStringTag && symToStringTag in Object(value))
	    ? getRawTag(value)
	    : objectToString(value);
	}
	
	module.exports = baseGetTag;


/***/ }),

/***/ 94:
/***/ (function(module, exports, __webpack_require__) {

	var root = __webpack_require__(95);
	
	/** Built-in value references. */
	var Symbol = root.Symbol;
	
	module.exports = Symbol;


/***/ }),

/***/ 95:
/***/ (function(module, exports, __webpack_require__) {

	var freeGlobal = __webpack_require__(96);
	
	/** Detect free variable `self`. */
	var freeSelf = typeof self == 'object' && self && self.Object === Object && self;
	
	/** Used as a reference to the global object. */
	var root = freeGlobal || freeSelf || Function('return this')();
	
	module.exports = root;


/***/ }),

/***/ 96:
/***/ (function(module, exports) {

	/* WEBPACK VAR INJECTION */(function(global) {/** Detect free variable `global` from Node.js. */
	var freeGlobal = typeof global == 'object' && global && global.Object === Object && global;
	
	module.exports = freeGlobal;
	
	/* WEBPACK VAR INJECTION */}.call(exports, (function() { return this; }())))

/***/ }),

/***/ 97:
/***/ (function(module, exports, __webpack_require__) {

	var Symbol = __webpack_require__(94);
	
	/** Used for built-in method references. */
	var objectProto = Object.prototype;
	
	/** Used to check objects for own properties. */
	var hasOwnProperty = objectProto.hasOwnProperty;
	
	/**
	 * Used to resolve the
	 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
	 * of values.
	 */
	var nativeObjectToString = objectProto.toString;
	
	/** Built-in value references. */
	var symToStringTag = Symbol ? Symbol.toStringTag : undefined;
	
	/**
	 * A specialized version of `baseGetTag` which ignores `Symbol.toStringTag` values.
	 *
	 * @private
	 * @param {*} value The value to query.
	 * @returns {string} Returns the raw `toStringTag`.
	 */
	function getRawTag(value) {
	  var isOwn = hasOwnProperty.call(value, symToStringTag),
	      tag = value[symToStringTag];
	
	  try {
	    value[symToStringTag] = undefined;
	    var unmasked = true;
	  } catch (e) {}
	
	  var result = nativeObjectToString.call(value);
	  if (unmasked) {
	    if (isOwn) {
	      value[symToStringTag] = tag;
	    } else {
	      delete value[symToStringTag];
	    }
	  }
	  return result;
	}
	
	module.exports = getRawTag;


/***/ }),

/***/ 98:
/***/ (function(module, exports) {

	/** Used for built-in method references. */
	var objectProto = Object.prototype;
	
	/**
	 * Used to resolve the
	 * [`toStringTag`](http://ecma-international.org/ecma-262/7.0/#sec-object.prototype.tostring)
	 * of values.
	 */
	var nativeObjectToString = objectProto.toString;
	
	/**
	 * Converts `value` to a string using `Object.prototype.toString`.
	 *
	 * @private
	 * @param {*} value The value to convert.
	 * @returns {string} Returns the converted string.
	 */
	function objectToString(value) {
	  return nativeObjectToString.call(value);
	}
	
	module.exports = objectToString;


/***/ }),

/***/ 105:
/***/ (function(module, exports, __webpack_require__) {

	var baseGetTag = __webpack_require__(93),
	    isObjectLike = __webpack_require__(106);
	
	/** `Object#toString` result references. */
	var symbolTag = '[object Symbol]';
	
	/**
	 * Checks if `value` is classified as a `Symbol` primitive or object.
	 *
	 * @static
	 * @memberOf _
	 * @since 4.0.0
	 * @category Lang
	 * @param {*} value The value to check.
	 * @returns {boolean} Returns `true` if `value` is a symbol, else `false`.
	 * @example
	 *
	 * _.isSymbol(Symbol.iterator);
	 * // => true
	 *
	 * _.isSymbol('abc');
	 * // => false
	 */
	function isSymbol(value) {
	  return typeof value == 'symbol' ||
	    (isObjectLike(value) && baseGetTag(value) == symbolTag);
	}
	
	module.exports = isSymbol;


/***/ }),

/***/ 106:
/***/ (function(module, exports) {

	/**
	 * Checks if `value` is object-like. A value is object-like if it's not `null`
	 * and has a `typeof` result of "object".
	 *
	 * @static
	 * @memberOf _
	 * @since 4.0.0
	 * @category Lang
	 * @param {*} value The value to check.
	 * @returns {boolean} Returns `true` if `value` is object-like, else `false`.
	 * @example
	 *
	 * _.isObjectLike({});
	 * // => true
	 *
	 * _.isObjectLike([1, 2, 3]);
	 * // => true
	 *
	 * _.isObjectLike(_.noop);
	 * // => false
	 *
	 * _.isObjectLike(null);
	 * // => false
	 */
	function isObjectLike(value) {
	  return value != null && typeof value == 'object';
	}
	
	module.exports = isObjectLike;


/***/ }),

/***/ 114:
/***/ (function(module, exports) {

	/**
	 * Checks if `value` is classified as an `Array` object.
	 *
	 * @static
	 * @memberOf _
	 * @since 0.1.0
	 * @category Lang
	 * @param {*} value The value to check.
	 * @returns {boolean} Returns `true` if `value` is an array, else `false`.
	 * @example
	 *
	 * _.isArray([1, 2, 3]);
	 * // => true
	 *
	 * _.isArray(document.body.children);
	 * // => false
	 *
	 * _.isArray('abc');
	 * // => false
	 *
	 * _.isArray(_.noop);
	 * // => false
	 */
	var isArray = Array.isArray;
	
	module.exports = isArray;


/***/ }),

/***/ 156:
/***/ (function(module, exports) {

	/**
	 * A specialized version of `_.map` for arrays without support for iteratee
	 * shorthands.
	 *
	 * @private
	 * @param {Array} [array] The array to iterate over.
	 * @param {Function} iteratee The function invoked per iteration.
	 * @returns {Array} Returns the new mapped array.
	 */
	function arrayMap(array, iteratee) {
	  var index = -1,
	      length = array == null ? 0 : array.length,
	      result = Array(length);
	
	  while (++index < length) {
	    result[index] = iteratee(array[index], index, array);
	  }
	  return result;
	}
	
	module.exports = arrayMap;


/***/ }),

/***/ 221:
/***/ (function(module, exports, __webpack_require__) {

	var baseToString = __webpack_require__(222);
	
	/**
	 * Converts `value` to a string. An empty string is returned for `null`
	 * and `undefined` values. The sign of `-0` is preserved.
	 *
	 * @static
	 * @memberOf _
	 * @since 4.0.0
	 * @category Lang
	 * @param {*} value The value to convert.
	 * @returns {string} Returns the converted string.
	 * @example
	 *
	 * _.toString(null);
	 * // => ''
	 *
	 * _.toString(-0);
	 * // => '-0'
	 *
	 * _.toString([1, 2, 3]);
	 * // => '1,2,3'
	 */
	function toString(value) {
	  return value == null ? '' : baseToString(value);
	}
	
	module.exports = toString;


/***/ }),

/***/ 222:
/***/ (function(module, exports, __webpack_require__) {

	var Symbol = __webpack_require__(94),
	    arrayMap = __webpack_require__(156),
	    isArray = __webpack_require__(114),
	    isSymbol = __webpack_require__(105);
	
	/** Used as references for various `Number` constants. */
	var INFINITY = 1 / 0;
	
	/** Used to convert symbols to primitives and strings. */
	var symbolProto = Symbol ? Symbol.prototype : undefined,
	    symbolToString = symbolProto ? symbolProto.toString : undefined;
	
	/**
	 * The base implementation of `_.toString` which doesn't convert nullish
	 * values to empty strings.
	 *
	 * @private
	 * @param {*} value The value to process.
	 * @returns {string} Returns the string.
	 */
	function baseToString(value) {
	  // Exit early for strings to avoid a performance hit in some environments.
	  if (typeof value == 'string') {
	    return value;
	  }
	  if (isArray(value)) {
	    // Recursively convert values (susceptible to call stack limits).
	    return arrayMap(value, baseToString) + '';
	  }
	  if (isSymbol(value)) {
	    return symbolToString ? symbolToString.call(value) : '';
	  }
	  var result = (value + '');
	  return (result == '0' && (1 / value) == -INFINITY) ? '-0' : result;
	}
	
	module.exports = baseToString;


/***/ }),

/***/ 321:
/***/ (function(module, exports) {

	// removed by extract-text-webpack-plugin

/***/ }),

/***/ 323:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/* Taken from here: http://stackoverflow.com/a/17907562 */
	function getInternetExplorerVersion(){
	  var ua,
	    re,
	    rv = -1;
	  
	  // check for edge
	  var isEdge = /(?:\b(MS)?IE\s+|\bTrident\/7\.0;.*\s+rv:|\bEdge\/)(\d+)/.test(navigator.userAgent);
	  if ( isEdge ) {
	    rv = parseInt("12");
	    return rv;
	  }
	  
	  var isIE11 = /Trident.*rv[ :]*11\./.test(navigator.userAgent);
	  if ( isIE11 ) {
	    rv = parseInt("11");
	    return rv;
	  }
	  if ( navigator.appName === "Microsoft Internet Explorer" ) {
	    ua = navigator.userAgent;
	    re = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
	    if ( re.exec(ua) !== null ) {
	      rv = parseFloat(RegExp.$1);
	    }
	  } else if ( navigator.appName === "Netscape" ) {
	    ua = navigator.userAgent;
	    re = new RegExp("Trident/.*rv:([0-9]{1,}[\\.0-9]{0,})");
	    if ( re.exec(ua) !== null ) {
	      rv = parseFloat(RegExp.$1);
	    }
	  }
	  return rv;
	}
	
	function showBrowserWarningIfRequired(){
	  var version = getInternetExplorerVersion();
	  console.log("Browser Version =" + version);
	  if ( version > 0 && version <= 11 ) {
	    d3.select("#browserCheck").classed("hidden", false);
	    d3.select("#killWarning").classed("hidden", true);
	    d3.select("#optionsArea").classed("hidden", true);
	    d3.select("#logo").classed("hidden", true);
	  }
	  if ( version == 12 ) {
	    d3.select("#logo").classed("hidden", false);
	    d3.select("#browserCheck").classed("hidden", false);
	    // connect the button;
	    var pb_kill = d3.select("#killWarning");
	    pb_kill.on("click", function (){
	      console.log("hide the warning please");
	      d3.select("#browserCheck").classed("hidden", true);
	      d3.select("#logo").style("padding", "10px");
	    });
	  }
	  else {
	    d3.select("#logo").classed("hidden", false);
	    d3.select("#browserCheck").classed("hidden", true);
	  }
	  
	}
	
	module.exports = showBrowserWarningIfRequired;
	showBrowserWarningIfRequired();
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 324:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {String.prototype.replaceAll = function ( search, replacement ){
	  var target = this;
	  return target.split(search).join(replacement);
	};
	module.exports = function (){
	  var newOntologyCounter = 1;
	  var app = {},
	    graph = webvowl.graph(),
	    options = graph.graphOptions(),
	    languageTools = webvowl.util.languageTools(),
	    GRAPH_SELECTOR = "#graph",
	    // Modules for the webvowl app
	    exportMenu = __webpack_require__(325)(graph),
	    filterMenu = __webpack_require__(327)(graph),
	    gravityMenu = __webpack_require__(328)(graph),
	    modeMenu = __webpack_require__(329)(graph),
	    debugMenu = __webpack_require__(330)(graph),
	    ontologyMenu = __webpack_require__(331)(graph),
	    pauseMenu = __webpack_require__(335)(graph),
	    resetMenu = __webpack_require__(336)(graph),
	    searchMenu = __webpack_require__(337)(graph),
	    navigationMenu = __webpack_require__(338)(graph),
	    zoomSlider = __webpack_require__(339)(graph),
	    // sidebar = require("./sidebar")(graph),
	    // leftSidebar = require("./leftSidebar")(graph),
	    // editSidebar = require("./editSidebar")(graph),
	    configMenu = __webpack_require__(340)(graph),
	    loadingModule = __webpack_require__(341)(graph),
	    warningModule = __webpack_require__(342)(graph),
	    directInputMod = __webpack_require__(343)(graph),
	    
	    
	    // Graph modules
	    colorExternalsSwitch = webvowl.modules.colorExternalsSwitch(graph),
	    compactNotationSwitch = webvowl.modules.compactNotationSwitch(graph),
	    datatypeFilter = webvowl.modules.datatypeFilter(),
	    disjointFilter = webvowl.modules.disjointFilter(),
	    focuser = webvowl.modules.focuser(graph),
	    emptyLiteralFilter = webvowl.modules.emptyLiteralFilter(),
	    nodeDegreeFilter = webvowl.modules.nodeDegreeFilter(filterMenu),
	    nodeScalingSwitch = webvowl.modules.nodeScalingSwitch(graph),
	    objectPropertyFilter = webvowl.modules.objectPropertyFilter(),
	    pickAndPin = webvowl.modules.pickAndPin(),
	    // selectionDetailDisplayer = webvowl.modules.selectionDetailsDisplayer(sidebar.updateSelectionInformation),
	    statistics = webvowl.modules.statistics(),
	    subclassFilter = webvowl.modules.subclassFilter(),
	    setOperatorFilter = webvowl.modules.setOperatorFilter();
	  
	  
	  app.getOptions = function (){
	    return webvowl.opts;
	  };
	  app.getGraph = function (){
	    return webvowl.gr;
	  };
	  // app.afterInitializationCallback=undefined;
	  
	  
	  var executeFileDrop = false;
	  var wasMessageToShow = false;
	  var firstTime = false;
	  
	  function addFileDropEvents( selector ){
	    var node = d3.select(selector);
	    
	    node.node().ondragover = function ( e ){
	      e.preventDefault();
	
	      d3.select("#dragDropContainer").classed("hidden", false);
	      // get svg size
	      var w = graph.options().width();
	      var h = graph.options().height();
	      
	      // get event position; (using clientX and clientY);
	      var cx = e.clientX;
	      var cy = e.clientY;
	      
	      if ( firstTime === false ) {
	        var state = d3.select("#loading-info").classed("hidden");
	        wasMessageToShow = !state;
	        firstTime = true;
	        d3.select("#loading-info").classed("hidden", true); // hide it so it does not conflict with drop event
	        var bb=d3.select("#drag_msg").node().getBoundingClientRect();
	        var hs = bb.height;
	        var ws = bb.width;
	        
	        var icon_scale=Math.min(hs,ws);
	        icon_scale/=100;
	        
	        d3.select("#drag_icon_group").attr("transform", "translate ( " + 0.25 * ws + " " + 0.25 * hs + ")");
	        d3.select("#drag_icon").attr("transform","matrix ("+icon_scale+",0,0,"+icon_scale+",0,0)");
	        d3.select("#drag_icon_drop").attr("transform","matrix ("+icon_scale+",0,0,"+icon_scale+",0,0)");
	      }
	      
	      
	      if ( (cx > 0.25 * w && cx < 0.75 * w) && (cy > 0.25 * h && cy < 0.75 * h) ) {
	        
	        d3.select("#drag_msg_text").node().innerHTML = "Drop it here.";
	        d3.select("#drag_msg").style("background-color", "#67bc0f");
	        d3.select("#drag_msg").style("color", "#000000");
	        executeFileDrop = true;
	        // d3.select("#drag_svg").transition()
	        //   .duration(100)
	        //   // .attr("-webkit-transform", "rotate(90)")
	        //   // .attr("-moz-transform",    "rotate(90)")
	        //   // .attr("-o-transform",      "rotate(90)")
	        //   .attr("transform",         "rotate(90)");
	  
	        d3.select("#drag_icon").classed("hidden",true);
	        d3.select("#drag_icon_drop").classed("hidden",false);
	  
	  
	      } else {
	        d3.select("#drag_msg_text").node().innerHTML = "Drag ontology file here.";
	        d3.select("#drag_msg").style("background-color", "#fefefe");
	        d3.select("#drag_msg").style("color", "#000000");
	        executeFileDrop = false;
	  
	        d3.select("#drag_icon").classed("hidden",false);
	        d3.select("#drag_icon_drop").classed("hidden",true);
	        
	        
	        // d3.select("#drag_svg").transition()
	        //   .duration(100)
	        //   // .attr("-webkit-transform", "rotate(0)")
	        //   // .attr("-moz-transform",    "rotate(0)")
	        //   // .attr("-o-transform",      "rotate(0)")
	        //   .attr("transform",         "rotate(0)");
	        //
	      }
	      
	    };
	    node.node().ondrop = function ( ev ){
	      ev.preventDefault();
	      firstTime = false;
	      if ( executeFileDrop ) {
	        if ( ev.dataTransfer.items ) {
	          if ( ev.dataTransfer.items.length === 1 ) {
	            if ( ev.dataTransfer.items[0].kind === 'file' ) {
	              var file = ev.dataTransfer.items[0].getAsFile();
	              graph.options().loadingModule().fromFileDrop(file.name, file);
	            }
	          }
	          else {
	            //  >> WARNING not multiple file uploaded;
	            graph.options().warningModule().showMultiFileUploadWarning();
	          }
	        }
	      }
	      d3.select("#dragDropContainer").classed("hidden", true);
	    };
	    
	    node.node().ondragleave = function ( e ){
	      var w = graph.options().width();
	      var h = graph.options().height();
	      
	      // get event position; (using clientX and clientY);
	      var cx = e.clientX;
	      var cy = e.clientY;
	      
	      var hidden = false;
	      firstTime = false;
	      
	      if ( cx < 0.1 * w || cx > 0.9 * w ) hidden = true;
	      if ( cy < 0.1 * h || cy > 0.9 * h ) hidden = true;
	      d3.select("#dragDropContainer").classed("hidden", hidden);
	      
	      d3.select("#loading-info").classed("hidden", !wasMessageToShow); // show it again
	      // check if it should be visible
	      var should_show=graph.options().loadingModule().getMessageVisibilityStatus();
	      if (should_show===false){
	        d3.select("#loading-info").classed("hidden", true); // hide it
	      }
	    };
	    
	  }
	  
	  
	  app.initialize = function (){
	    /* OLE Customization */
	    // addFileDropEvents(GRAPH_SELECTOR);
	    
	    window.requestAnimationFrame = window.requestAnimationFrame || window.mozRequestAnimationFrame || window.webkitRequestAnimationFrame || window.msRequestAnimationFrame || function ( f ){
	        return setTimeout(f, 1000 / 60);
	      }; // simulate calling code 60
	    window.cancelAnimationFrame = window.cancelAnimationFrame || window.mozCancelAnimationFrame || function ( requestID ){
	        clearTimeout(requestID);
	      }; //fall back
	    
	    options.graphContainerSelector(GRAPH_SELECTOR);
	    options.selectionModules().push(focuser);
	    // options.selectionModules().push(selectionDetailDisplayer);
	    options.selectionModules().push(pickAndPin);
	    
	    options.filterModules().push(emptyLiteralFilter);
	    options.filterModules().push(statistics);
	    
	    options.filterModules().push(nodeDegreeFilter);
	    options.filterModules().push(datatypeFilter);
	    options.filterModules().push(objectPropertyFilter);
	    options.filterModules().push(subclassFilter);
	    options.filterModules().push(disjointFilter);
	    options.filterModules().push(setOperatorFilter);
	    options.filterModules().push(nodeScalingSwitch);
	    options.filterModules().push(compactNotationSwitch);
	    options.filterModules().push(colorExternalsSwitch);
	    
	    d3.select(window).on("resize", adjustSize);
	    
	    exportMenu.setup();
	    gravityMenu.setup();
	    filterMenu.setup(datatypeFilter, objectPropertyFilter, subclassFilter, disjointFilter, setOperatorFilter, nodeDegreeFilter);
	    modeMenu.setup(pickAndPin, nodeScalingSwitch, compactNotationSwitch, colorExternalsSwitch);
	    pauseMenu.setup();
	    // sidebar.setup();
	    loadingModule.setup();
	    // leftSidebar.setup();
	    // editSidebar.setup();
	    debugMenu.setup();
	    var agentVersion = getInternetExplorerVersion();
	    if ( agentVersion > 0 && agentVersion <= 11 ) {
	      console.log("Agent version " + agentVersion);
	      console.log("This agent is not supported");
	      d3.select("#browserCheck").classed("hidden", false);
	      d3.select("#killWarning").classed("hidden", true);
	      d3.select("#optionsArea").classed("hidden", true);
	      d3.select("#logo").classed("hidden", true);
	    } else {
	      d3.select("#logo").classed("hidden", false);
	      if ( agentVersion === 12 ) {
	        // allow Mircosoft Edge Browser but with warning
	        d3.select("#browserCheck").classed("hidden", false);
	        d3.select("#killWarning").classed("hidden", false);
	      } else {
	        d3.select("#browserCheck").classed("hidden", true);
	      }
	      
	      resetMenu.setup([gravityMenu, filterMenu, modeMenu, focuser, pauseMenu]);
	      searchMenu.setup();
	      navigationMenu.setup();
	      zoomSlider.setup();
	      
	      // give the options the pointer to the some menus for import and export
	      options.literalFilter(emptyLiteralFilter);
	      options.nodeDegreeFilter(nodeDegreeFilter);
	      options.loadingModule(loadingModule);
	      options.filterMenu(filterMenu);
	      options.modeMenu(modeMenu);
	      options.gravityMenu(gravityMenu);
	      options.pausedMenu(pauseMenu);
	      options.pickAndPinModule(pickAndPin);
	      options.resetMenu(resetMenu);
	      options.searchMenu(searchMenu);
	      options.ontologyMenu(ontologyMenu);
	      options.navigationMenu(navigationMenu);
	      // options.sidebar(sidebar);
	      // options.leftSidebar(leftSidebar);
	      // options.editSidebar(editSidebar);
	      options.exportMenu(exportMenu);
	      options.graphObject(graph);
	      options.zoomSlider(zoomSlider);
	      options.warningModule(warningModule);
	      options.directInputModule(directInputMod);
	      options.datatypeFilter(datatypeFilter);
	      options.objectPropertyFilter(objectPropertyFilter);
	      options.subclassFilter(subclassFilter);
	      options.setOperatorFilter(setOperatorFilter);
	      options.disjointPropertyFilter(disjointFilter);
	      options.focuserModule(focuser);
	      options.colorExternalsModule(colorExternalsSwitch);
	      options.compactNotationModule(compactNotationSwitch);
	      
	      ontologyMenu.setup(loadOntologyFromText);
	      configMenu.setup();
	      
	      // leftSidebar.showSidebar(0);
	      // leftSidebar.hideCollapseButton(true);
	      
	      
	      graph.start();
	      
	      var modeOp = d3.select("#modeOfOperationString");
	      modeOp.style("font-size", "0.6em");
	      modeOp.style("font-style", "italic");
	      
	      adjustSize();
	      var defZoom;
	      var w = graph.options().width();
	      var h = graph.options().height();
	      defZoom = Math.min(w, h) / 1000;
	      
	      var hideDebugOptions = true;
	      if ( hideDebugOptions === false ) {
	        graph.setForceTickFunctionWithFPS();
	      }
	      
	      graph.setDefaultZoom(defZoom);
	      d3.selectAll(".debugOption").classed("hidden", hideDebugOptions);
	      
	      // prevent backspace reloading event
	      var htmlBody = d3.select("body");
	      d3.select(document).on("keydown", function ( e ){
	        if ( d3.event.keyCode === 8 && d3.event.target === htmlBody.node() ) {
	          // we could add here an alert
	          d3.event.preventDefault();
	        }
	        // using ctrl+Shift+d as debug option
	        if ( d3.event.ctrlKey && d3.event.shiftKey && d3.event.keyCode === 68 ) {
	          graph.options().executeHiddenDebugFeatuers();
	          d3.event.preventDefault();
	        }
	      });
	      if ( d3.select("#maxLabelWidthSliderOption") ) {
	        var setValue = !graph.options().dynamicLabelWidth();
	        d3.select("#maxLabelWidthSlider").node().disabled = setValue;
	        d3.select("#maxLabelWidthvalueLabel").classed("disabledLabelForSlider", setValue);
	        d3.select("#maxLabelWidthDescriptionLabel").classed("disabledLabelForSlider", setValue);
	      }
	      
	      d3.select("#blockGraphInteractions").style("position", "absolute")
	        .style("top", "0")
	        .style("background-color", "#bdbdbd")
	        .style("opacity", "0.5")
	        .style("pointer-events", "auto")
	        .style("width", graph.options().width() + "px")
	        .style("height", graph.options().height() + "px")
	        .on("click", function (){
	          d3.event.preventDefault();
	          d3.event.stopPropagation();
	        })
	        .on("dblclick", function (){
	          d3.event.preventDefault();
	          d3.event.stopPropagation();
	        });
	      
	      d3.select("#direct-text-input").on("click", function (){
	        directInputMod.setDirectInputMode();
	      });
	      d3.select("#blockGraphInteractions").node().draggable = false;
	      options.prefixModule(webvowl.util.prefixTools(graph));
	      adjustSize();
	      /* OLE Customization */
	      // sidebar.updateOntologyInformation(undefined, statistics);
	      // loadingModule.parseUrlAndLoadOntology(); // loads automatically the ontology provided by the parameters
	      options.debugMenu(debugMenu);
	      debugMenu.updateSettings();
	      
	      // connect the reloadCachedVersionButton
	      d3.select("#reloadSvgIcon").on("click", function (){
	        if ( d3.select("#reloadSvgIcon").node().disabled === true ) {
	          graph.options().ontologyMenu().clearCachedVersion();
	          return;
	        }
	        d3.select("#reloadCachedOntology").classed("hidden", true);
	        graph.options().ontologyMenu().reloadCachedOntology();
	        
	      });
	      // add the initialized objects
	      webvowl.opts = options;
	      webvowl.gr = graph;
	      
	    }
	  };
	  
	  
	  function loadOntologyFromText( jsonText, filename, alternativeFilename , isJson ){
	    d3.select("#reloadCachedOntology").classed("hidden", true);
	    pauseMenu.reset();
	    graph.options().navigationMenu().hideAllMenus();
	    
	    if ( (jsonText === undefined && filename === undefined) || (jsonText.length === 0) ) {
	      loadingModule.notValidJsonFile();
	      return;
	    }
	    graph.editorMode(); // updates the checkbox
	    var data;
	    if(isJson){
	      data = jsonText;
	    }
	    else if ( jsonText ) {
	      // validate JSON FILE
	      var validJSON;
	      try {
	        data = JSON.parse(jsonText);
	        validJSON = true;
	      } catch ( e ) {
	        validJSON = false;
	      }
	      if ( validJSON === false ) {
	        // the server output is not a valid json file
	        loadingModule.notValidJsonFile();
	        return;
	      }
	      
	      if ( !filename ) {
	        // First look if an ontology title exists, otherwise take the alternative filename
	        var ontologyNames = data.header ? data.header.title : undefined;
	        var ontologyName = languageTools.textInLanguage(ontologyNames);
	        
	        if ( ontologyName ) {
	          filename = ontologyName;
	        } else {
	          filename = alternativeFilename;
	        }
	      }
	    }
	    
	    // check if we have graph data
	    var classCount = 0;
	    if ( data.class !== undefined ) {
	      classCount = data.class.length;
	    }
	    
	    var loadEmptyOntologyForEditing = false;
	    if ( location.hash.indexOf("#new_ontology") !== -1 ) {
	      loadEmptyOntologyForEditing = true;
	      newOntologyCounter++;
	      d3.select("#empty").node().href = "#opts=editorMode=true;#new_ontology" + newOntologyCounter;
	    }
	    if ( classCount === 0 && graph.editorMode() === false && loadEmptyOntologyForEditing === false ) {
	      // generate message for the user;
	      loadingModule.emptyGraphContentError();
	    } else {
	      loadingModule.validJsonFile();
	      ontologyMenu.setCachedOntology(filename, jsonText);
	      exportMenu.setJsonText(jsonText);
	      options.data(data);
	      graph.options().loadingModule().setPercentMode();
	      if ( loadEmptyOntologyForEditing === true ) {
	        graph.editorMode(true);
	        
	      }
	
	      graph.load();
	      // sidebar.updateOntologyInformation(data, statistics);
	      exportMenu.setFilename(filename);
	      graph.updateZoomSliderValueFromOutside();
	      adjustSize();
	      
	      var flagOfCheckBox = d3.select("#editorModeModuleCheckbox").node().checked;
	      graph.editorMode(flagOfCheckBox);// update gui
	      
	    }
	  }
	  
	  function adjustSize(){
	    var graphContainer = d3.select(GRAPH_SELECTOR),
	      svg = graphContainer.select("svg"),
	      height = window.innerHeight - 40,
	      // width = window.innerWidth - (window.innerWidth * 0.22);
	
	      /* OLE Customization */
	      width = $("#graph").width();
	
	    // if ( sidebar.getSidebarVisibility() === "0" ) {
	    //   height = window.innerHeight - 40;
	    //   width = window.innerWidth;
	    // }
	    
	    directInputMod.updateLayout();
	    d3.select("#blockGraphInteractions").style("width", window.innerWidth + "px");
	    d3.select("#blockGraphInteractions").style("height", window.innerHeight + "px");
	    
	    d3.select("#WarningErrorMessagesContainer").style("width", width + "px");
	    d3.select("#WarningErrorMessagesContainer").style("height", height + "px");
	    
	    d3.select("#WarningErrorMessages").style("max-height", (height - 12) + "px");
	    
	    graphContainer.style("height", height + "px");
	
	     /* OLE Customization */
	    svg.attr("width", width)
	      .attr("height", height - $("#swipeBarContainer").height());
	     /* OLE Customization */
	    options.width(width)
	      .height( height - $("#swipeBarContainer").height());
	    
	    graph.updateStyle();
	    
	    if ( isTouchDevice() === true ) {
	      if ( graph.isEditorMode() === true )
	        d3.select("#modeOfOperationString").node().innerHTML = "touch able device detected";
	      graph.setTouchDevice(true);
	      
	    } else {
	      if ( graph.isEditorMode() === true )
	        d3.select("#modeOfOperationString").node().innerHTML = "point & click device detected";
	      graph.setTouchDevice(false);
	    }
	    
	    d3.select("#loadingInfo-container").style("height", 0.5 * (height - 80) + "px");
	    loadingModule.checkForScreenSize();
	    
	    adjustSliderSize();
	    // update also the padding options of loading and the logo positions;
	    var warningDiv = d3.select("#browserCheck");
	    if ( warningDiv.classed("hidden") === false ) {
	      var offset = 10 + warningDiv.node().getBoundingClientRect().height;
	      d3.select("#logo").style("padding", offset + "px 10px");
	    } else {
	      // remove the dynamic padding from the logo element;
	      d3.select("#logo").style("padding", "10px");
	    }
	    
	    // scrollbar tests;
	    var element = d3.select("#menuElementContainer").node();
	    var maxScrollLeft = element.scrollWidth - element.clientWidth;
	    var leftButton = d3.select("#scrollLeftButton");
	    var rightButton = d3.select("#scrollRightButton");
	    if ( maxScrollLeft > 0 ) {
	      // show both and then check how far is bar;
	      rightButton.classed("hidden", false);
	      leftButton.classed("hidden", false);
	      navigationMenu.updateScrollButtonVisibility();
	    } else {
	      // hide both;
	      rightButton.classed("hidden", true);
	      leftButton.classed("hidden", true);
	    }
	    
	    // adjust height of the leftSidebar element;
	    // editSidebar.updateElementWidth();
	    
	    /* OLE Customization */
	    // var hs = d3.select("#drag_msg").node().getBoundingClientRect().height;
	    // var ws = d3.select("#drag_msg").node().getBoundingClientRect().width;
	    // d3.select("#drag_icon_group").attr("transform", "translate ( " + 0.25 * ws + " " + 0.25 * hs + ")");
	    
	  }
	  
	  function adjustSliderSize(){
	    // TODO: refactor and put this into the slider it self
	    var height = window.innerHeight - 40;
	    var fullHeight = height;
	    var zoomOutPos = height - 30;
	    var sliderHeight = 150;
	    
	    // assuming DOM elements are generated in the index.html
	    // todo: refactor for independent usage of graph and app
	    if ( fullHeight < 150 ) {
	      // hide the slider button;
	      d3.select("#zoomSliderParagraph").classed("hidden", true);
	      d3.select("#zoomOutButton").classed("hidden", true);
	      d3.select("#zoomInButton").classed("hidden", true);
	      d3.select("#centerGraphButton").classed("hidden", true);
	      return;
	    }
	    d3.select("#zoomSliderParagraph").classed("hidden", false);
	    d3.select("#zoomOutButton").classed("hidden", false);
	    d3.select("#zoomInButton").classed("hidden", false);
	    d3.select("#centerGraphButton").classed("hidden", false);
	    
	    var zoomInPos = zoomOutPos - 20;
	    var centerPos = zoomInPos - 20;
	    if ( fullHeight < 280 ) {
	      // hide the slider button;
	      d3.select("#zoomSliderParagraph").classed("hidden", true);//var sliderPos=zoomOutPos-sliderHeight;
	      d3.select("#zoomOutButton").style("top", zoomOutPos + "px");
	      d3.select("#zoomInButton").style("top", zoomInPos + "px");
	      d3.select("#centerGraphButton").style("top", centerPos + "px");
	      return;
	    }
	    
	    var sliderPos = zoomOutPos - sliderHeight;
	    zoomInPos = sliderPos - 20;
	    centerPos = zoomInPos - 20;
	    d3.select("#zoomSliderParagraph").classed("hidden", false);
	    d3.select("#zoomOutButton").style("top", zoomOutPos + "px");
	    d3.select("#zoomInButton").style("top", zoomInPos + "px");
	    d3.select("#centerGraphButton").style("top", centerPos + "px");
	    d3.select("#zoomSliderParagraph").style("top", sliderPos + "px");
	  }
	  
	  function isTouchDevice(){
	    try {
	      document.createEvent("TouchEvent");
	      return true;
	    } catch ( e ) {
	      return false;
	    }
	  }
	  
	  
	  function getInternetExplorerVersion(){
	    var ua,
	      re,
	      rv = -1;
	    
	    // check for edge
	    var isEdge = /(?:\b(MS)?IE\s+|\bTrident\/7\.0;.*\s+rv:|\bEdge\/)(\d+)/.test(navigator.userAgent);
	    if ( isEdge ) {
	      rv = parseInt("12");
	      return rv;
	    }
	    
	    var isIE11 = /Trident.*rv[ :]*11\./.test(navigator.userAgent);
	    if ( isIE11 ) {
	      rv = parseInt("11");
	      return rv;
	    }
	    if ( navigator.appName === "Microsoft Internet Explorer" ) {
	      ua = navigator.userAgent;
	      re = new RegExp("MSIE ([0-9]{1,}[\\.0-9]{0,})");
	      if ( re.exec(ua) !== null ) {
	        rv = parseFloat(RegExp.$1);
	      }
	    } else if ( navigator.appName === "Netscape" ) {
	      ua = navigator.userAgent;
	      re = new RegExp("Trident/.*rv:([0-9]{1,}[\\.0-9]{0,})");
	      if ( re.exec(ua) !== null ) {
	        rv = parseFloat(RegExp.$1);
	      }
	    }
	    return rv;
	  }
	  
	  return app;
	}
	;
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 325:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the logic for the export button.
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  
	  var exportMenu = {},
	    exportSvgButton,
	    exportFilename,
	    exportJsonButton,
	    exportTurtleButton,
	    exportTexButton,
	    copyButton,
	    exportableJsonText;
	  
	  var exportTTLModule = __webpack_require__(326)(graph);
	  
	  
	  String.prototype.replaceAll = function ( search, replacement ){
	    var target = this;
	    return target.split(search).join(replacement);
	  };
	  
	  
	  /**
	   * Adds the export button to the website.
	   */
	  exportMenu.setup = function (){
	    exportSvgButton = d3.select("#exportSvg")
	      .on("click", exportSvg);
	    exportJsonButton = d3.select("#exportJson")
	      .on("click", exportJson);
	    
	    copyButton = d3.select("#copyBt")
	      .on("click", copyUrl);
	    
	    exportTexButton = d3.select("#exportTex")
	      .on("click", exportTex);
	    
	    exportTurtleButton = d3.select("#exportTurtle")
	      .on("click", exportTurtle);
	    
	    var menuEntry = d3.select("#m_export");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	      exportMenu.exportAsUrl();
	    });
	  };
	  function exportTurtle(){
	    var success = exportTTLModule.requestExport();
	    var result = exportTTLModule.resultingTTL_Content();
	    console.log("Exporter was successful: " + success);
	    if ( success ) {
	      // console.log("The result is : " + result);
	      var ontoTitle = "NewOntology";
	      // var ontoTitle=graph.options().getGeneralMetaObjectProperty('title');
	      // if (ontoTitle===undefined || ontoTitle.length===0)
	      // 	ontoTitle="NewOntology";
	      // else{
	      // 	// language object -.-
	      //    ontoTitle.replace(" ","_")
	      // }
	      
	      // TODO: show TEXT in warning module?
	      
	      
	      // // write the data
	      var dataURI = "data:text/json;charset=utf-8," + encodeURIComponent(result);
	      
	      exportTurtleButton.attr("href", dataURI)
	        .attr("download", ontoTitle + ".ttl");
	      
	      // okay restore old href?
	      //  exportTurtleButton.attr("href", oldHref);
	    } else {
	      console.log("ShowWarning!");
	      graph.options().warningModule().showExporterWarning();
	      console.log("Stay on the page! " + window.location.href);
	      exportTurtleButton.attr("href", window.location.href);
	      d3.event.preventDefault(); // prevent the href to be called ( reloads the page otherwise )
	      
	      
	    }
	  }
	  
	  exportMenu.setFilename = function ( filename ){
	    exportFilename = filename || "export";
	  };
	  
	  exportMenu.setJsonText = function ( jsonText ){
	    exportableJsonText = jsonText;
	  };
	  
	  function copyUrl(){
	    d3.select("#exportedUrl").node().focus();
	    d3.select("#exportedUrl").node().select();
	    document.execCommand("copy");
	    graph.options().navigationMenu().hideAllMenus();
	    d3.event.preventDefault(); // prevent the href to be called ( reloads the page otherwise )
	  }
	  
	  function prepareOptionString( defOpts, currOpts ){
	    var setOptions = 0;
	    var optsString = "opts=";
	    
	    for ( var name in defOpts ) {
	      // define key and value ;
	      if ( defOpts.hasOwnProperty(name) ) {// for travis warning
	        var def_value = defOpts[name];
	        var cur_value = currOpts[name];
	        if ( def_value !== cur_value ) {
	          optsString += name + "=" + cur_value + ";";
	          setOptions++;
	        }
	      }
	    }
	    optsString += "";
	    if ( setOptions === 0 ) {
	      return "";
	    }
	    return optsString;
	  }
	  
	  exportMenu.exportAsUrl = function (){
	    var currObj = {};
	    // currObj.sidebar = graph.options().sidebar().getSidebarVisibility();
	    
	    // identify default value given by ontology;
	    var defOntValue = graph.options().filterMenu().getDefaultDegreeValue();
	    var currentValue = graph.options().filterMenu().getDegreeSliderValue();
	    if ( parseInt(defOntValue) === parseInt(currentValue) ) {
	      currObj.doc = -1;
	    } else {
	      currObj.doc = currentValue;
	    }
	    
	    currObj.cd = graph.options().classDistance();
	    currObj.dd = graph.options().datatypeDistance();
	    if ( graph.editorMode() === true ) {
	      currObj.editorMode = "true";
	    } else {
	      currObj.editorMode = "false";
	    }
	    currObj.filter_datatypes = String(graph.options().filterMenu().getCheckBoxValue("datatypeFilterCheckbox"));
	    currObj.filter_sco = String(graph.options().filterMenu().getCheckBoxValue("subclassFilterCheckbox"));
	    currObj.filter_disjoint = String(graph.options().filterMenu().getCheckBoxValue("disjointFilterCheckbox"));
	    currObj.filter_setOperator = String(graph.options().filterMenu().getCheckBoxValue("setoperatorFilterCheckbox"));
	    currObj.filter_objectProperties = String(graph.options().filterMenu().getCheckBoxValue("objectPropertyFilterCheckbox"));
	    currObj.mode_dynamic = String(graph.options().dynamicLabelWidth());
	    currObj.mode_scaling = String(graph.options().modeMenu().getCheckBoxValue("nodescalingModuleCheckbox"));
	    currObj.mode_compact = String(graph.options().modeMenu().getCheckBoxValue("compactnotationModuleCheckbox"));
	    currObj.mode_colorExt = String(graph.options().modeMenu().getCheckBoxValue("colorexternalsModuleCheckbox"));
	    currObj.mode_multiColor = String(graph.options().modeMenu().colorModeState());
	    currObj.mode_pnp = String(graph.options().modeMenu().getCheckBoxValue("pickandpinModuleCheckbox"));
	    currObj.debugFeatures = String(!graph.options().getHideDebugFeatures());
	    currObj.rect = 0;
	    
	    var defObj = graph.options().initialConfig();
	    var optsString = prepareOptionString(defObj, currObj);
	    var urlString = String(location);
	    var htmlElement;
	    // when everything is default then there is nothing to write
	    if ( optsString.length === 0 ) {
	      // building up parameter list;
	      
	      // remove the all options form location
	      var hashCode = location.hash;
	      urlString = urlString.split(hashCode)[0];
	      
	      var lPos = hashCode.lastIndexOf("#");
	      if ( lPos === -1 ) {
	        htmlElement = d3.select("#exportedUrl").node();
	        htmlElement.value = String(location);
	        htmlElement.title = String(location);
	        return;  // nothing to change in the location String
	      }
	      var newURL = hashCode.slice(lPos, hashCode.length);
	      htmlElement = d3.select("#exportedUrl").node();
	      htmlElement.value = urlString + newURL;
	      htmlElement.title = urlString + newURL;
	      return;
	    }
	    
	    // generate the options string;
	    var numParameters = (urlString.match(/#/g) || []).length;
	    var newUrlString;
	    if ( numParameters === undefined || numParameters === 0 ) {
	      newUrlString = urlString + "#" + optsString;
	    }
	    if ( numParameters > 0 ) {
	      var tokens = urlString.split("#");
	      var i;
	      if ( tokens[1].indexOf("opts=") >= 0 ) {
	        tokens[1] = optsString;
	        newUrlString = tokens[0];
	      } else {
	        newUrlString = tokens[0] + "#";
	        newUrlString += optsString;
	      }
	      // append parameters
	      for ( i = 1; i < tokens.length; i++ ) {
	        if ( tokens[i].length > 0 ) {
	          newUrlString += "#" + tokens[i];
	        }
	      }
	    }
	    // building up parameter list;
	    htmlElement = d3.select("#exportedUrl").node();
	    htmlElement.value = newUrlString;
	    htmlElement.title = newUrlString;
	    
	  };
	  
	  function exportSvg(){
	    graph.options().navigationMenu().hideAllMenus();
	    // Get the d3js SVG element
	    var graphSvg = d3.select(graph.options().graphContainerSelector()).select("svg"),
	      graphSvgCode,
	      escapedGraphSvgCode,
	      dataURI;
	    
	    // inline the styles, so that the exported svg code contains the css rules
	    inlineVowlStyles();
	    hideNonExportableElements();
	    
	    graphSvgCode = graphSvg.attr("version", 1.1)
	      .attr("xmlns", "http://www.w3.org/2000/svg")
	      .node().parentNode.innerHTML;
	    
	    // Insert the reference to VOWL
	    graphSvgCode = "<!-- Created with WebVOWL (version " + webvowl.version + ")" +
	      ", http://vowl.visualdataweb.org -->\n" + graphSvgCode;
	    
	    escapedGraphSvgCode = escapeUnicodeCharacters(graphSvgCode);
	    //btoa(); Creates a base-64 encoded ASCII string from a "string" of binary data.
	    dataURI = "data:image/svg+xml;base64," + btoa(escapedGraphSvgCode);
	    
	    
	    exportSvgButton.attr("href", dataURI)
	      .attr("download", exportFilename + ".svg");
	    
	    // remove graphic styles for interaction to go back to normal
	    removeVowlInlineStyles();
	    showNonExportableElements();
	    graph.lazyRefresh();
	  }
	  
	  function escapeUnicodeCharacters( text ){
	    var textSnippets = [],
	      i, textLength = text.length,
	      character,
	      charCode;
	    
	    for ( i = 0; i < textLength; i++ ) {
	      character = text.charAt(i);
	      charCode = character.charCodeAt(0);
	      
	      if ( charCode < 128 ) {
	        textSnippets.push(character);
	      } else {
	        textSnippets.push("&#" + charCode + ";");
	      }
	    }
	    
	    return textSnippets.join("");
	  }
	  
	  function inlineVowlStyles(){
	    setStyleSensitively(".text", [{ name: "font-family", value: "Helvetica, Arial, sans-serif" }, {
	      name: "font-size",
	      value: "12px"
	    }]);
	    setStyleSensitively(".subtext", [{ name: "font-size", value: "9px" }]);
	    setStyleSensitively(".text.instance-count", [{ name: "fill", value: "#666" }]);
	    setStyleSensitively(".external + text .instance-count", [{ name: "fill", value: "#aaa" }]);
	    setStyleSensitively(".cardinality", [{ name: "font-size", value: "10px" }]);
	    setStyleSensitively(".text, .embedded", [{ name: "pointer-events", value: "none" }]);
	    setStyleSensitively(".class, .object, .disjoint, .objectproperty, .disjointwith, .equivalentproperty, .transitiveproperty, .functionalproperty, .inversefunctionalproperty, .symmetricproperty, .allvaluesfromproperty, .somevaluesfromproperty", [{
	      name: "fill",
	      value: "#acf"
	    }]);
	    setStyleSensitively(".label .datatype, .datatypeproperty", [{ name: "fill", value: "#9c6" }]);
	    setStyleSensitively(".rdf, .rdfproperty", [{ name: "fill", value: "#c9c" }]);
	    setStyleSensitively(".literal, .node .datatype", [{ name: "fill", value: "#fc3" }]);
	    setStyleSensitively(".deprecated, .deprecatedproperty", [{ name: "fill", value: "#ccc" }]);
	    setStyleSensitively(".external, .externalproperty", [{ name: "fill", value: "#36c" }]);
	    setStyleSensitively("path, .nofill", [{ name: "fill", value: "none" }]);
	    setStyleSensitively("marker path", [{ name: "fill", value: "#000" }]);
	    setStyleSensitively(".class, path, line, .fineline", [{ name: "stroke", value: "#000" }]);
	    setStyleSensitively(".white, .subclass, .subclassproperty, .external + text", [{ name: "fill", value: "#fff" }]);
	    setStyleSensitively(".class.hovered, .property.hovered, .cardinality.hovered, .cardinality.focused, circle.pin, .filled.hovered, .filled.focused", [{
	      name: "fill",
	      value: "#f00"
	    }, { name: "cursor", value: "pointer" }]);
	    setStyleSensitively(".focused, path.hovered", [{ name: "stroke", value: "#f00" }]);
	    setStyleSensitively(".indirect-highlighting, .feature:hover", [{ name: "fill", value: "#f90" }]);
	    setStyleSensitively(".values-from", [{ name: "stroke", value: "#69c" }]);
	    setStyleSensitively(".symbol, .values-from.filled", [{ name: "fill", value: "#69c" }]);
	    setStyleSensitively(".class, path, line", [{ name: "stroke-width", value: "2" }]);
	    setStyleSensitively(".fineline", [{ name: "stroke-width", value: "1" }]);
	    setStyleSensitively(".dashed, .anonymous", [{ name: "stroke-dasharray", value: "8" }]);
	    setStyleSensitively(".dotted", [{ name: "stroke-dasharray", value: "3" }]);
	    setStyleSensitively("rect.focused, circle.focused", [{ name: "stroke-width", value: "4px" }]);
	    setStyleSensitively(".nostroke", [{ name: "stroke", value: "none" }]);
	    setStyleSensitively("marker path", [{ name: "stroke-dasharray", value: "100" }]);
	  }
	  
	  function setStyleSensitively( selector, styles ){
	    var elements = d3.selectAll(selector);
	    if ( elements.empty() ) {
	      return;
	    }
	    
	    styles.forEach(function ( style ){
	      elements.each(function (){
	        var element = d3.select(this);
	        if ( !shouldntChangeInlineCss(element, style.name) ) {
	          element.style(style.name, style.value);
	        }
	      });
	    });
	  }
	  
	  function shouldntChangeInlineCss( element, style ){
	    return style === "fill" && hasBackgroundColorSet(element);
	  }
	  
	  function hasBackgroundColorSet( element ){
	    var data = element.datum();
	    if ( data === undefined ) {
	      return false;
	    }
	    return data.backgroundColor && !!data.backgroundColor();
	  }
	  
	  /**
	   * For example the pin of the pick&pin module should be invisible in the exported graphic.
	   */
	  function hideNonExportableElements(){
	    d3.selectAll(".hidden-in-export").style("display", "none");
	  }
	  
	  function removeVowlInlineStyles(){
	    d3.selectAll(".text, .subtext, .text.instance-count, .external + text .instance-count, .cardinality, .text, .embedded, .class, .object, .disjoint, .objectproperty, .disjointwith, .equivalentproperty, .transitiveproperty, .functionalproperty, .inversefunctionalproperty, .symmetricproperty, .allvaluesfromproperty, .somevaluesfromproperty, .label .datatype, .datatypeproperty, .rdf, .rdfproperty, .literal, .node .datatype, .deprecated, .deprecatedproperty, .external, .externalproperty, path, .nofill, .symbol, .values-from.filled, marker path, .class, path, line, .fineline, .white, .subclass, .subclassproperty, .external + text, .class.hovered, .property.hovered, .cardinality.hovered, .cardinality.focused, circle.pin, .filled.hovered, .filled.focused, .focused, path.hovered, .indirect-highlighting, .feature:hover, .values-from, .class, path, line, .fineline, .dashed, .anonymous, .dotted, rect.focused, circle.focused, .nostroke, marker path")
	      .each(function (){
	        var element = d3.select(this);
	        
	        var inlineStyles = element.node().style;
	        for ( var styleName in inlineStyles ) {
	          if ( inlineStyles.hasOwnProperty(styleName) ) {
	            if ( shouldntChangeInlineCss(element, styleName) ) {
	              continue;
	            }
	            element.style(styleName, null);
	          }
	        }
	        
	        if ( element.datum && element.datum() !== undefined && element.datum().type ) {
	          if ( element.datum().type() === "rdfs:subClassOf" ) {
	            element.style("fill", null);
	          }
	        }
	      });
	    
	    // repair svg icons in the menu;
	    var scrollContainer = d3.select("#menuElementContainer").node();
	    var controlElements = scrollContainer.children;
	    var numEntries = controlElements.length;
	    
	    for ( var i = 0; i < numEntries; i++ ) {
	      var currentMenu = controlElements[i].id;
	      d3.select("#" + currentMenu).select("path").style("stroke-width", "0");
	      d3.select("#" + currentMenu).select("path").style("fill", "#fff");
	    }
	    
	    d3.select("#magnifyingGlass").style("stroke-width", "0");
	    d3.select("#magnifyingGlass").style("fill", "#666");
	    
	  }
	  
	  function showNonExportableElements(){
	    d3.selectAll(".hidden-in-export").style("display", null);
	  }
	  
	  function exposeVariable(nodes){
		
	
	    var classIndividualElements = [];
	    var nIndividuals = nodes;
	    for ( j = 0; j < nIndividuals.length; j++ ) {
	      var indObj = {};
	      //indObj.iri = nIndividuals[j].iri();
	      //indObj.baseIri = nIndividuals[j].baseIri();
	      indObj.labels = nIndividuals[j].label();
	     
	     
	      classIndividualElements.push(indObj);
	    }
	      
	    return classIndividualElements;
	  }
	  
	  exportMenu.createJSON_exportObject = function (){
	    var i, j, k; // an index variable for the for-loops
	    
	    /** get data for exporter **/
	      
	      // extract onotology information;
	    var unfilteredData = graph.getUnfilteredData();
	    var ontologyComment = graph.options().data()._comment;
	    var metaObj = graph.options().getGeneralMetaObject();
	    var header = graph.options().data().header;
	    
	    if ( metaObj.iri && metaObj.iri !== header.iri ) {
	      header.iri = metaObj.iri;
	    }
	    if ( metaObj.title && metaObj.title !== header.title ) {
	      header.title = metaObj.title;
	    }
	    if ( metaObj.version && metaObj.version !== header.version ) {
	      header.version = metaObj.version;
	    }
	    if ( metaObj.author && metaObj.author !== header.author ) {
	      header.author = metaObj.author;
	    }
	    if ( metaObj.description && metaObj.description !== header.description ) {
	      header.description = metaObj.description;
	    }
	    
	    
	    var exportText = {};
	    exportText._comment = ontologyComment;
	    exportText.header = header;
	    exportText.namespace = graph.options().data().namespace;
	    if ( exportText.namespace === undefined ) {
	      exportText.namespace = []; // just an empty namespace array
	    }
	    // we do have now the unfiltered data which needs to be transfered to class/classAttribute and property/propertyAttribute
	    
	    
	    // var classAttributeString='classAttribute:[ \n';
	    var nodes = unfilteredData.nodes;
	    var nLen = nodes.length; // hope for compiler unroll
	    var classObjects = [];
	    var classAttributeObjects = [];
	    for ( i = 0; i < nLen; i++ ) {
	      var classObj = {};
	      var classAttr = {};
	      classObj.id = nodes[i].id();
	      classObj.type = nodes[i].type();
	      classObjects.push(classObj);
	      
	      // define the attributes object
	      classAttr.id = nodes[i].id();
	      classAttr.iri = nodes[i].iri();
	      classAttr.baseIri = nodes[i].baseIri();
	      classAttr.label = nodes[i].label();
	      
	      if ( nodes[i].attributes().length > 0 ) {
	        classAttr.attributes = nodes[i].attributes();
	      }
	      if ( nodes[i].comment() ) {
	        classAttr.comment = nodes[i].comment();
	      }
	      if ( nodes[i].annotations() ) {
	        classAttr.annotations = nodes[i].annotations();
	      }
	      if ( nodes[i].description() ) {
	        classAttr.description = nodes[i].description();
	      }
	      
	      
	      if ( nodes[i].individuals().length > 0 ) {
	        var classIndividualElements = [];
	        var nIndividuals = nodes[i].individuals();
	        for ( j = 0; j < nIndividuals.length; j++ ) {
	          var indObj = {};
	          indObj.iri = nIndividuals[j].iri();
	          indObj.baseIri = nIndividuals[j].baseIri();
	          indObj.labels = nIndividuals[j].label();
	          if ( nIndividuals[j].annotations() ) {
	            indObj.annotations = nIndividuals[j].annotations();
	          }
	          if ( nIndividuals[j].description() ) {
	            indObj.description = nIndividuals[j].description();
	          }
	          if ( nIndividuals[j].comment() ) {
	            indObj.comment = nIndividuals[j].comment();
	          }
	          classIndividualElements.push(indObj);
	        }
	        classAttr.individuals = classIndividualElements;
	      }
	      
	      if ( nodes[i].terminalPoints().length > 0 ) {
	        var terminalPoints = nodes[i].terminalPoints();
	        var classIndividualElements = exposeVariable(terminalPoints);
	        classAttr.terminalPoints = classIndividualElements;
	      }
	      if ( nodes[i].wavelengths().length > 0 ) {
	        var wavelengths = nodes[i].wavelengths();
	        var classIndividualElements = exposeVariable(wavelengths);
	        classAttr.wavelengths = classIndividualElements;
	      }
	
	
	      var equalsForAttributes = undefined;
	      if ( nodes[i].equivalents().length > 0 ) {
	        equalsForAttributes = [];
	        var equals = nodes[i].equivalents();
	        for ( j = 0; j < equals.length; j++ ) {
	          var eqObj = {};
	          var eqAttr = {};
	          eqObj.id = equals[j].id();
	          equalsForAttributes.push(equals[j].id());
	          eqObj.type = equals[j].type();
	          classObjects.push(eqObj);
	          
	          eqAttr.id = equals[j].id();
	          eqAttr.iri = equals[j].iri();
	          eqAttr.baseIri = equals[j].baseIri();
	          eqAttr.label = equals[j].label();
	          
	          if ( equals[j].attributes().length > 0 ) {
	            eqAttr.attributes = equals[j].attributes();
	          }
	          if ( equals[j].comment() ) {
	            eqAttr.comment = equals[j].comment();
	          }
	          if ( equals[j].individuals().length > 0 ) {
	            eqAttr.individuals = equals[j].individuals();
	          }
	          if ( equals[j].annotations() ) {
	            eqAttr.annotations = equals[j].annotations();
	          }
	          if ( equals[j].description() ) {
	            eqAttr.description = equals[j].description();
	          }
	          
	          if ( equals[j].individuals().length > 0 ) {
	            var e_classIndividualElements = [];
	            var e_nIndividuals = equals[i].individuals();
	            for ( k = 0; k < e_nIndividuals.length; k++ ) {
	              var e_indObj = {};
	              e_indObj.iri = e_nIndividuals[k].iri();
	              e_indObj.baseIri = e_nIndividuals[k].baseIri();
	              e_indObj.labels = e_nIndividuals[k].label();
	              
	              if ( e_nIndividuals[k].annotations() ) {
	                e_indObj.annotations = e_nIndividuals[k].annotations();
	              }
	              if ( e_nIndividuals[k].description() ) {
	                e_indObj.description = e_nIndividuals[k].description();
	              }
	              if ( e_nIndividuals[k].comment() ) {
	                e_indObj.comment = e_nIndividuals[k].comment();
	              }
	              e_classIndividualElements.push(e_indObj);
	            }
	            eqAttr.individuals = e_classIndividualElements;
	          }
	          
	          classAttributeObjects.push(eqAttr);
	        }
	      }
	      if ( equalsForAttributes && equalsForAttributes.length > 0 ) {
	        classAttr.equivalent = equalsForAttributes;
	      }
	      
	      // classAttr.subClasses=nodes[i].subClasses(); // not needed
	      // classAttr.instances=nodes[i].instances();
	      
	      //
	      // .complement(element.complement)
	      // .disjointUnion(element.disjointUnion)
	      // .description(element.description)
	      // .equivalents(element.equivalent)
	      // .intersection(element.intersection)
	      // .type(element.type) Ignore, because we predefined it
	      // .union(element.union)
	      classAttributeObjects.push(classAttr);
	    }
	    
	    /** -- properties -- **/
	    var properties = unfilteredData.properties;
	    var pLen = properties.length; // hope for compiler unroll
	    var propertyObjects = [];
	    var propertyAttributeObjects = [];
	    
	    for ( i = 0; i < pLen; i++ ) {
	      var pObj = {};
	      var pAttr = {};
	      pObj.id = properties[i].id();
	      pObj.type = properties[i].type();
	      propertyObjects.push(pObj);
	      
	      // // define the attributes object
	      pAttr.id = properties[i].id();
	      pAttr.iri = properties[i].iri();
	      pAttr.baseIri = properties[i].baseIri();
	      pAttr.label = properties[i].label();
	      
	      if ( properties[i].attributes().length > 0 ) {
	        pAttr.attributes = properties[i].attributes();
	      }
	      if ( properties[i].comment() ) {
	        pAttr.comment = properties[i].comment();
	      }
	      
	      if ( properties[i].annotations() ) {
	        pAttr.annotations = properties[i].annotations();
	      }
	      if ( properties[i].maxCardinality() ) {
	        pAttr.maxCardinality = properties[i].maxCardinality();
	      }
	      if ( properties[i].minCardinality() ) {
	        pAttr.minCardinality = properties[i].minCardinality();
	      }
	      if ( properties[i].cardinality() ) {
	        pAttr.cardinality = properties[i].cardinality();
	      }
	      if ( properties[i].description() ) {
	        pAttr.description = properties[i].description();
	      }
	      
	      pAttr.domain = properties[i].domain().id();
	      pAttr.range = properties[i].range().id();
	      // sub properties;
	      if ( properties[i].subproperties() ) {
	        var subProps = properties[i].subproperties();
	        var subPropsIdArray = [];
	        for ( j = 0; j < subProps.length; j++ ) {
	          if ( subProps[j].id )
	            subPropsIdArray.push(subProps[j].id());
	        }
	        pAttr.subproperty = subPropsIdArray;
	      }
	      
	      // super properties
	      if ( properties[i].superproperties() ) {
	        var superProps = properties[i].superproperties();
	        var superPropsIdArray = [];
	        for ( j = 0; j < superProps.length; j++ ) {
	          if ( superProps[j].id )
	            superPropsIdArray.push(superProps[j].id());
	        }
	        pAttr.superproperty = superPropsIdArray;
	      }
	      
	      // check for inverse element
	      if ( properties[i].inverse() ) {
	        if ( properties[i].inverse().id )
	          pAttr.inverse = properties[i].inverse().id();
	      }
	      propertyAttributeObjects.push(pAttr);
	    }
	    
	    exportText.class = classObjects;
	    exportText.classAttribute = classAttributeObjects;
	    exportText.property = propertyObjects;
	    exportText.propertyAttribute = propertyAttributeObjects;
	    
	    
	    var nodeElements = graph.graphNodeElements();  // get visible nodes
	    var propElements = graph.graphLabelElements(); // get visible labels
	    // var jsonObj = JSON.parse(exportableJsonText);	   // reparse the original input json
	    
	    /** modify comment **/
	    var comment = exportText._comment;
	    var additionalString = " [Additional Information added by WebVOWL Exporter Version: " + "1.1.5" + "]";
	    // adding new string to comment only if it does not exist
	    if ( comment.indexOf(additionalString) === -1 ) {
	      exportText._comment = comment + " [Additional Information added by WebVOWL Exporter Version: " + "1.1.5" + "]";
	    }
	    
	    var classAttribute = exportText.classAttribute;
	    var propAttribute = exportText.propertyAttribute;
	    /**  remove previously stored variables **/
	    for ( i = 0; i < classAttribute.length; i++ ) {
	      var classObj_del = classAttribute[i];
	      delete classObj_del.pos;
	      delete classObj_del.pinned;
	    }
	    var propertyObj;
	    for ( i = 0; i < propAttribute.length; i++ ) {
	      propertyObj = propAttribute[i];
	      delete propertyObj.pos;
	      delete propertyObj.pinned;
	    }
	    /**  add new variables to jsonObj  **/
	    // class attribute variables
	    nodeElements.each(function ( node ){
	      var nodeId = node.id();
	      for ( i = 0; i < classAttribute.length; i++ ) {
	        var classObj = classAttribute[i];
	        if ( classObj.id === nodeId ) {
	          // store relative positions
	          classObj.pos = [parseFloat(node.x.toFixed(2)), parseFloat(node.y.toFixed(2))];
	          if ( node.pinned() )
	            classObj.pinned = true;
	          break;
	        }
	      }
	    });
	    // property attribute variables
	    for ( j = 0; j < propElements.length; j++ ) {
	      var correspondingProp = propElements[j].property();
	      for ( i = 0; i < propAttribute.length; i++ ) {
	        propertyObj = propAttribute[i];
	        if ( propertyObj.id === correspondingProp.id() ) {
	          propertyObj.pos = [parseFloat(propElements[j].x.toFixed(2)), parseFloat(propElements[j].y.toFixed(2))];
	          if ( propElements[j].pinned() )
	            propertyObj.pinned = true;
	          break;
	        }
	      }
	    }
	    /** create the variable for settings and set their values **/
	    exportText.settings = {};
	    
	    // Global Settings
	    var zoom = graph.scaleFactor();
	    var paused = graph.paused();
	    var translation = [parseFloat(graph.translation()[0].toFixed(2)), parseFloat(graph.translation()[1].toFixed(2))];
	    exportText.settings.global = {};
	    exportText.settings.global.zoom = zoom.toFixed(2);
	    exportText.settings.global.translation = translation;
	    exportText.settings.global.paused = paused;
	    
	    // shared variable declaration
	    var cb_text;
	    var isEnabled;
	    var cb_obj;
	    
	    // Gravity Settings
	    var classDistance = graph.options().classDistance();
	    var datatypeDistance = graph.options().datatypeDistance();
	    exportText.settings.gravity = {};
	    exportText.settings.gravity.classDistance = classDistance;
	    exportText.settings.gravity.datatypeDistance = datatypeDistance;
	    
	    // Filter Settings
	    var fMenu = graph.options().filterMenu();
	    var fContainer = fMenu.getCheckBoxContainer();
	    var cbCont = [];
	    for ( i = 0; i < fContainer.length; i++ ) {
	      cb_text = fContainer[i].checkbox.attr("id");
	      isEnabled = fContainer[i].checkbox.property("checked");
	      cb_obj = {};
	      cb_obj.id = cb_text;
	      cb_obj.checked = isEnabled;
	      cbCont.push(cb_obj);
	    }
	    var degreeSliderVal = fMenu.getDegreeSliderValue();
	    exportText.settings.filter = {};
	    exportText.settings.filter.checkBox = cbCont;
	    exportText.settings.filter.degreeSliderValue = degreeSliderVal;
	    
	    // Modes Settings
	    var mMenu = graph.options().modeMenu();
	    var mContainer = mMenu.getCheckBoxContainer();
	    var cb_modes = [];
	    for ( i = 0; i < mContainer.length; i++ ) {
	      cb_text = mContainer[i].attr("id");
	      isEnabled = mContainer[i].property("checked");
	      cb_obj = {};
	      cb_obj.id = cb_text;
	      cb_obj.checked = isEnabled;
	      cb_modes.push(cb_obj);
	    }
	    var colorSwitchState = mMenu.colorModeState();
	    exportText.settings.modes = {};
	    exportText.settings.modes.checkBox = cb_modes;
	    exportText.settings.modes.colorSwitchState = colorSwitchState;
	    
	    var exportObj = {};
	    // todo: [ ] find better way for ordering the objects
	    // hack for ordering of objects, so settings is after metrics
	    exportObj._comment = exportText._comment;
	    exportObj.header = exportText.header;
	    exportObj.namespace = exportText.namespace;
	    exportObj.metrics = exportText.metrics;
	    exportObj.settings = exportText.settings;
	    exportObj.class = exportText.class;
	    exportObj.classAttribute = exportText.classAttribute;
	    exportObj.property = exportText.property;
	    exportObj.propertyAttribute = exportText.propertyAttribute;
	    
	    return exportObj;
	  };
	  
	  function exportJson(){
	    graph.options().navigationMenu().hideAllMenus();
	    /**  check if there is data **/
	    if ( !exportableJsonText ) {
	      alert("No graph data available.");
	      // Stop the redirection to the path of the href attribute
	      d3.event.preventDefault();
	      return;
	    }
	    
	    var exportObj = exportMenu.createJSON_exportObject();
	    
	    // make a string again;
	    var exportText = JSON.stringify(exportObj, null, '  ');
	    // write the data
	    var dataURI = "data:text/json;charset=utf-8," + encodeURIComponent(exportText);
	    var jsonExportFileName = exportFilename;
	    
	    if ( !jsonExportFileName.endsWith(".json") )
	      jsonExportFileName += ".json";
	    exportJsonButton.attr("href", dataURI)
	      .attr("download", jsonExportFileName);
	  }
	  
	  var curveFunction = d3.svg.line()
	    .x(function ( d ){
	      return d.x;
	    })
	    .y(function ( d ){
	      return d.y;
	    })
	    .interpolate("cardinal");
	  var loopFunction = d3.svg.line()
	    .x(function ( d ){
	      return d.x;
	    })
	    .y(function ( d ){
	      return d.y;
	    })
	    .interpolate("cardinal")
	    .tension(-1);
	  
	  function exportTex(){
	    var zoom = graph.scaleFactor();
	    var grTranslate = graph.translation();
	    var bbox = graph.getBoundingBoxForTex();
	    var comment = " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n";
	    comment += " %        Generated with the experimental alpha version of the TeX exporter of WebVOWL (version 1.1.3) %%% \n";
	    comment += " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n";
	    comment += " %   The content can be used as import in other TeX documents. \n";
	    comment += " %   Parent document has to use the following packages   \n";
	    comment += " %   \\usepackage{tikz}  \n";
	    comment += " %   \\usepackage{helvet}  \n";
	    comment += " %   \\usetikzlibrary{decorations.markings,decorations.shapes,decorations,arrows,automata,backgrounds,petri,shapes.geometric}  \n";
	    comment += " %   \\usepackage{xcolor}  \n\n";
	    comment += " %%%%%%%%%%%%%%% Example Parent Document %%%%%%%%%%%%%%%%%%%%%%%\n";
	    comment += " %\\documentclass{article} \n";
	    comment += " %\\usepackage{tikz} \n";
	    comment += " %\\usepackage{helvet} \n";
	    comment += " %\\usetikzlibrary{decorations.markings,decorations.shapes,decorations,arrows,automata,backgrounds,petri,shapes.geometric} \n";
	    comment += " %\\usepackage{xcolor} \n\n";
	    comment += " %\\begin{document} \n";
	    comment += " %\\section{Example} \n";
	    comment += " %  This is an example. \n";
	    comment += " %  \\begin{figure} \n";
	    comment += " %    \\input{<THIS_FILE_NAME>} % << tex file name for the graph \n";
	    comment += " %    \\caption{A generated graph with TKIZ using alpha version of the TeX exporter of WebVOWL (version 1.1.3) } \n";
	    comment += " %  \\end{figure} \n";
	    comment += " %\\end{document} \n";
	    comment += " %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%\n\n";
	    
	    
	    var texString = comment + "\\definecolor{imageBGCOLOR}{HTML}{FFFFFF} \n" +
	      "\\definecolor{owlClassColor}{HTML}{AACCFF}\n" +
	      "\\definecolor{owlObjectPropertyColor}{HTML}{AACCFF}\n" +
	      "\\definecolor{owlExternalClassColor}{HTML}{AACCFF}\n" +
	      "\\definecolor{owlDatatypePropertyColor}{HTML}{99CC66}\n" +
	      "\\definecolor{owlDatatypeColor}{HTML}{FFCC33}\n" +
	      "\\definecolor{owlThingColor}{HTML}{FFFFFF}\n" +
	      "\\definecolor{valuesFrom}{HTML}{6699CC}\n" +
	      "\\definecolor{rdfPropertyColor}{HTML}{CC99CC}\n" +
	      "\\definecolor{unionColor}{HTML}{6699cc}\n" +
	      "\\begin{center} \n" +
	      "\\resizebox{\\linewidth}{!}{\n" +
	      
	      "\\begin{tikzpicture}[framed]\n" +
	      "\\clip (" + bbox[0] + "pt , " + bbox[1] + "pt ) rectangle (" + bbox[2] + "pt , " + bbox[3] + "pt);\n" +
	      "\\tikzstyle{dashed}=[dash pattern=on 4pt off 4pt] \n" +
	      "\\tikzstyle{dotted}=[dash pattern=on 2pt off 2pt] \n" +
	      "\\fontfamily{sans-serif}{\\fontsize{12}{12}\\selectfont}\n \n";
	    
	    
	    texString += "\\tikzset{triangleBlack/.style = {fill=black, draw=black, line width=1pt,scale=0.7,regular polygon, regular polygon sides=3} }\n";
	    texString += "\\tikzset{triangleWhite/.style = {fill=white, draw=black, line width=1pt,scale=0.7,regular polygon, regular polygon sides=3} }\n";
	    texString += "\\tikzset{triangleBlue/.style  = {fill=valuesFrom, draw=valuesFrom, line width=1pt,scale=0.7,regular polygon, regular polygon sides=3} }\n";
	    
	    texString += "\\tikzset{Diamond/.style = {fill=white, draw=black, line width=2pt,scale=1.2,regular polygon, regular polygon sides=4} }\n";
	    
	    
	    texString += "\\tikzset{Literal/.style={rectangle,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, draw=black, dashed, line width=1pt, fill=owlDatatypeColor, minimum width=80pt,\n" +
	      "minimum height = 20pt}}\n\n";
	    
	    texString += "\\tikzset{Datatype/.style={rectangle,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, draw=black, line width=1pt, fill=owlDatatypeColor, minimum width=80pt,\n" +
	      "minimum height = 20pt}}\n\n";
	    
	    
	    texString += "\\tikzset{owlClass/.style={circle, inner sep=0mm,align=center, \n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, draw=black, line width=1pt, fill=owlClassColor, minimum size=101pt}}\n\n";
	    
	    texString += "\\tikzset{anonymousClass/.style={circle, inner sep=0mm,align=center, \n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, dashed, draw=black, line width=1pt, fill=owlClassColor, minimum size=101pt}}\n\n";
	    
	    
	    texString += "\\tikzset{owlThing/.style={circle, inner sep=0mm,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, dashed, draw=black, line width=1pt, fill=owlThingColor, minimum size=62pt}}\n\n";
	    
	    
	    texString += "\\tikzset{owlObjectProperty/.style={rectangle,align=center,\n" +
	      "inner sep=0mm,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "fill=owlObjectPropertyColor, minimum width=80pt,\n" +
	      "minimum height = 25pt}}\n\n";
	    
	    texString += "\\tikzset{rdfProperty/.style={rectangle,align=center,\n" +
	      "inner sep=0mm,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "fill=rdfPropertyColor, minimum width=80pt,\n" +
	      "minimum height = 25pt}}\n\n";
	    
	    
	    texString += "\\tikzset{owlDatatypeProperty/.style={rectangle,align=center,\n" +
	      "fill=owlDatatypePropertyColor, minimum width=80pt,\n" +
	      "inner sep=0mm,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "minimum height = 25pt}}\n\n";
	    
	    texString += "\\tikzset{rdfsSubClassOf/.style={rectangle,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "inner sep=0mm,\n" +
	      "fill=imageBGCOLOR, minimum width=80pt,\n" +
	      "minimum height = 25pt}}\n\n";
	    
	    texString += "\\tikzset{unionOf/.style={circle, inner sep=0mm,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, draw=black, line width=1pt, fill=unionColor, minimum size=25pt}}\n\n";
	    
	    texString += "\\tikzset{disjointWith/.style={circle, inner sep=0mm,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "black, draw=black, line width=1pt, fill=unionColor, minimum size=20pt}}\n\n";
	    
	    
	    texString += "\\tikzset{owlEquivalentClass/.style={circle,align=center,\n" +
	      "font={\\fontsize{12pt}{12}\\selectfont \\sffamily },\n" +
	      "inner sep=0mm,\n" +
	      "black, solid, draw=black, line width=3pt, fill=owlExternalClassColor, minimum size=101pt,\n" +
	      "postaction = {draw,line width=1pt, white}}}\n\n";
	    
	    // draw a bounding box;
	    
	    // get bbox coordinates;
	    
	    
	    graph.options().navigationMenu().hideAllMenus();
	    /**  check if there is data **/
	    if ( !exportableJsonText ) {
	      alert("No graph data available.");
	      // Stop the redirection to the path of the href attribute
	      d3.event.preventDefault();
	      return;
	    }
	    
	    var i = 0, identifier;
	    
	    /** get data for exporter **/
	    var nodeElements = graph.graphNodeElements();  // get visible nodes
	    var propElements = graph.graphLabelElements(); // get visible labels
	    var links = graph.graphLinkElements();
	    
	    // export only nodes;
	    // draw Links;
	    for ( i = 0; i < links.length; i++ ) {
	      var link = links[i];
	      // console.log("\n****************\nInverstigating Link for property "+link.property().labelForCurrentLanguage());
	      
	      var prop = link.property();
	      var dx, dy, px, py, rx, ry;
	      var colorStr = "black";
	      var linkDomainIntersection;
	      var linkRangeIntersection;
	      var center;
	      var linkStyle = "";
	      var isLoop = "";
	      var curvePoint;
	      var pathStart;
	      var pathEnd;
	      var controlPoints;
	      var len;
	      var ahAngle;
	      var pathLen;
	      var markerOffset = 7;
	      
	      var arrowType = "triangleBlack";
	      var linkWidth = ",line width=2pt";
	      if ( prop.linkType ) {
	        if ( prop.linkType() === "dotted" ) {
	          //stroke-dasharray: 3;
	          linkStyle = ", dotted ";
	          arrowType = "triangleWhite";
	        }
	        if ( prop.linkType() === "dashed" ) {
	          //stroke-dasharray: 3;
	          linkStyle = ", dashed ";
	        }
	        
	        if ( prop.linkType() === "values-from" ) {
	          colorStr = "valuesFrom";
	        }
	        
	      }
	      
	      var startX, startY, endX, endY, normX, normY, lg;
	      
	      if ( link.layers().length === 1 && !link.loops() ) {
	        
	        linkDomainIntersection = graph.math().calculateIntersection(link.range(), link.domain(), 1);
	        linkRangeIntersection = graph.math().calculateIntersection(link.domain(), link.range(), 1);
	        center = graph.math().calculateCenter(linkDomainIntersection, linkRangeIntersection);
	        dx = linkDomainIntersection.x;
	        dy = -linkDomainIntersection.y;
	        px = center.x;
	        py = -center.y;
	        rx = linkRangeIntersection.x;
	        ry = -linkRangeIntersection.y;
	        
	        
	        pathStart = linkDomainIntersection;
	        curvePoint = center;
	        pathEnd = linkRangeIntersection;
	        
	        var nx = rx - px;
	        var ny = ry - py;
	        
	        // normalize ;
	        len = Math.sqrt(nx * nx + ny * ny);
	        
	        nx = nx / len;
	        ny = ny / len;
	        
	        ahAngle = Math.atan2(ny, nx) * (180 / Math.PI);
	        normX = nx;
	        normY = ny;
	      }
	      else {
	        if ( link.isLoop() ) {
	          isLoop = ", tension=3";
	          controlPoints = graph.math().calculateLoopPoints(link);
	          pathStart = controlPoints[0];
	          curvePoint = controlPoints[1];
	          pathEnd = controlPoints[2];
	        } else {
	          curvePoint = link.label();
	          pathStart = graph.math().calculateIntersection(curvePoint, link.domain(), 1);
	          pathEnd = graph.math().calculateIntersection(curvePoint, link.range(), 1);
	        }
	        dx = pathStart.x;
	        dy = -pathStart.y;
	        px = curvePoint.x;
	        py = -curvePoint.y;
	        rx = pathEnd.x;
	        ry = -pathEnd.y;
	      }
	      
	      texString += "\\draw [" + colorStr + linkStyle + linkWidth + isLoop + "] plot [smooth] coordinates {(" +
	        dx + "pt, " + dy + "pt) (" + px + "pt, " + py + "pt)  (" + rx + "pt, " + ry + "pt)};\n";
	      
	      
	      if ( link.property().markerElement() === undefined ) continue;
	      
	      // add arrow head;
	      
	      
	      if ( link.property().type() === "owl:someValuesFrom" || link.property().type() === "owl:allValuesFrom" ) {
	        arrowType = "triangleBlue";
	      }
	      
	      lg = link.pathObj();
	      pathLen = Math.floor(lg.node().getTotalLength());
	      var p1 = lg.node().getPointAtLength(pathLen - 4);
	      var p2 = lg.node().getPointAtLength(pathLen);
	      var markerCenter = lg.node().getPointAtLength(pathLen - 6);
	      
	      if ( link.property().type() === "setOperatorProperty" ) {
	        p1 = lg.node().getPointAtLength(4);
	        p2 = lg.node().getPointAtLength(0);
	        markerCenter = lg.node().getPointAtLength(8);
	        arrowType = "Diamond";
	      }
	      
	      startX = p1.x;
	      startY = p1.y;
	      endX = p2.x;
	      endY = p2.y;
	      normX = endX - startX;
	      normY = endY - startY;
	      len = Math.sqrt(normX * normX + normY * normY);
	      normX = normX / len;
	      normY = normY / len;
	      
	      ahAngle = -1.0 * Math.atan2(normY, normX) * (180 / Math.PI);
	      ahAngle -= 90;
	      if ( link.property().type() === "setOperatorProperty" ) {
	        ahAngle -= 45;
	      }
	      // console.log(link.property().labelForCurrentLanguage()+ ": "+normX+ " "+normY +"  "+ahAngle);
	      rx = markerCenter.x;
	      ry = markerCenter.y;
	      if ( link.layers().length === 1 && !link.loops() ) {
	        // markerOffset=-1*m
	        ry = -1 * ry;
	        texString += "\\node[" + arrowType + ", rotate=" + ahAngle + "] at (" + rx + "pt, " + ry + "pt)   (single_marker" + i + ") {};\n ";
	      } else {
	        ry = -1 * ry;
	        texString += "\\node[" + arrowType + ", rotate=" + ahAngle + "] at (" + rx + "pt, " + ry + "pt)   (marker" + i + ") {};\n ";
	      }
	      
	      // if   (link.isLoop()){
	      //    rotAngle=-10+angle * (180 / Math.PI);
	      // }
	      
	      // add cardinality;
	      var cardinalityText = link.property().generateCardinalityText();
	      if ( cardinalityText && cardinalityText.length > 0 ) {
	        var cardinalityCenter = lg.node().getPointAtLength(pathLen - 18);
	        var cx = cardinalityCenter.x - (10 * normY);
	        var cy = cardinalityCenter.y + (10 * normX); // using orthonormal y Coordinate
	        cy *= -1.0;
	        var textColor = "black";
	        if ( cardinalityText.indexOf("A") > -1 ) {
	          cardinalityText = "$\\forall$";
	        }
	        if ( cardinalityText.indexOf("E") > -1 ) {
	          cardinalityText = "$\\exists$";
	        }
	        
	        
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily },text=" + textColor + "] at (" + cx + "pt, " + cy + "pt)   (cardinalityText" + i + ") {" + cardinalityText + "};\n ";
	      }
	      
	      
	      if ( link.property().inverse() ) {
	        lg = link.pathObj();
	        pathLen = Math.floor(lg.node().getTotalLength());
	        var p1_inv = lg.node().getPointAtLength(4);
	        var p2_inv = lg.node().getPointAtLength(0);
	        var markerCenter_inv = lg.node().getPointAtLength(6);
	        startX = p1_inv.x;
	        startY = p1_inv.y;
	        endX = p2_inv.x;
	        endY = p2_inv.y;
	        normX = endX - startX;
	        normY = endY - startY;
	        len = Math.sqrt(normX * normX + normY * normY);
	        normX = normX / len;
	        normY = normY / len;
	        
	        ahAngle = -1.0 * Math.atan2(normY, normX) * (180 / Math.PI);
	        ahAngle -= 90;
	        //   console.log("INV>>\n "+link.property().inverse().labelForCurrentLanguage()+ ": "+normX+ " "+normY +"  "+ahAngle);
	        rx = markerCenter_inv.x;
	        ry = markerCenter_inv.y;
	        if ( link.layers().length === 1 && !link.loops() ) {
	          // markerOffset=-1*m
	          ry = -1 * ry;
	          texString += "\\node[" + arrowType + ", rotate=" + ahAngle + "] at (" + rx + "pt, " + ry + "pt)   (INV_single_marker" + i + ") {};\n ";
	        } else {
	          ry = -1 * ry;
	          texString += "\\node[" + arrowType + ", rotate=" + ahAngle + "] at (" + rx + "pt, " + ry + "pt)   (INV_marker" + i + ") {};\n ";
	        }
	      }
	      
	      
	    }
	    
	    
	    nodeElements.each(function ( node ){
	      
	      px = node.x;
	      py = -node.y;
	      identifier = node.labelForCurrentLanguage();
	      // console.log("Writing : "+ identifier);
	      if ( identifier === undefined ) identifier = "";
	      var qType = "owlClass";
	      if ( node.type() === "owl:Thing" || node.type() === "owl:Nothing" )
	        qType = "owlThing";
	      
	      if ( node.type() === "owl:equivalentClass" ) {
	        qType = "owlEquivalentClass";
	      }
	      var textColorStr = "";
	      if ( node.textBlock ) {
	        var txtColor = node.textBlock()._textBlock().style("fill");
	        if ( txtColor === "rgb(0, 0, 0)" ) {
	          textColorStr = ", text=black";
	        }
	        if ( txtColor === "rgb(255, 255, 255)" ) {
	          textColorStr = ", text=white";
	        }
	        
	        
	        var tspans = node.textBlock()._textBlock().node().children;
	        if ( tspans[0] ) {
	          identifier = tspans[0].innerHTML;
	          if ( node.individuals() && node.individuals().length === parseInt(tspans[0].innerHTML) ) {
	            identifier = "{\\color{gray} " + tspans[0].innerHTML + " }";
	          }
	          for ( var t = 1; t < tspans.length; t++ ) {
	            if ( node.individuals() && node.individuals().length === parseInt(tspans[t].innerHTML) ) {
	              identifier += "\\\\ {\\color{gray} " + tspans[t].innerHTML + " }";
	            } else {
	              identifier += "\\\\ {\\small " + tspans[t].innerHTML + " }";
	            }
	          }
	        }
	      }
	      if ( node.type() === "rdfs:Literal" ) {
	        qType = "Literal";
	      }
	      if ( node.type() === "rdfs:Datatype" ) {
	        qType = "Datatype";
	      }
	      if ( node.attributes().indexOf("anonymous") !== -1 ) {
	        qType = "anonymousClass";
	      }
	      
	      
	      if ( node.type() === "owl:unionOf" || node.type() === "owl:complementOf" || node.type() === "owl:disjointUnionOf" || node.type() === "owl:intersectionOf" )
	        qType = "owlClass";
	      
	      var bgColorStr = "";
	      var widthString = "";
	      
	      if ( node.type() === "rdfs:Literal" || node.type() === "rdfs:Datatype" ) {
	        var width = node.width();
	        widthString = ",minimum width=" + width + "pt";
	      }
	      else {
	        widthString = ",minimum size=" + 2 * node.actualRadius() + "pt";
	        
	      }
	      if ( node.backgroundColor() ) {
	        var bgColor = node.backgroundColor();
	        bgColor.toUpperCase();
	        bgColor = bgColor.slice(1, bgColor.length);
	        texString += "\\definecolor{Node" + i + "_COLOR}{HTML}{" + bgColor + "} \n ";
	        bgColorStr = ", fill=Node" + i + "_COLOR ";
	      }
	      if ( node.attributes().indexOf("deprecated") > -1 ) {
	        texString += "\\definecolor{Node" + i + "_COLOR}{HTML}{CCCCCC} \n ";
	        bgColorStr = ", fill=Node" + i + "_COLOR ";
	      }
	      
	      var leftPos = px - 7;
	      var rightPos = px + 7;
	      var txtOffset = py + 20;
	      if ( node.type() !== "owl:unionOf" || node.type() !== "owl:disjointUnionOf" ) {
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + px + "pt, " + py + "pt)   (Node" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	      }
	      if ( node.type() === "owl:unionOf" ) {
	        // add symbol to it;
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + px + "pt, " + py + "pt)   (Node" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + leftPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + rightPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[unionOf ,fill=none   , text=black] at (" + leftPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[text=black] at (" + px + "pt, " + py + "pt)  (unionText13) {$\\mathbf{\\cup}$};\n";
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + txtOffset + "pt)   (Node_text" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	      }
	      // OWL DISJOINT UNION OF
	      if ( node.type() === "owl:disjointUnionOf" ) {
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + px + "pt, " + py + "pt)   (Node" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + leftPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + rightPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[unionOf ,fill=none   , text=black] at (" + leftPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + py + "pt)  (disjointUnoinText" + i + ") {1};\n";
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + txtOffset + "pt)   (Node_text" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	      }
	      // OWL COMPLEMENT OF
	      if ( node.type() === "owl:complementOf" ) {
	        // add symbol to it;
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + px + "pt, " + py + "pt)   (Node" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + px + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[font={\\fontsize{18pt}{18}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + py + "pt)  (unionText13) {$\\neg$};\n";
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + txtOffset + "pt)   (Node_text" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	      }
	      // OWL INTERSECTION OF
	      if ( node.type() === "owl:intersectionOf" ) {
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + px + "pt, " + py + "pt)   (Node" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + leftPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[unionOf   , text=black] at (" + rightPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[unionOf ,fill=none   , text=black] at (" + leftPos + "pt, " + py + "pt)   (SymbolNode" + i + ") {};\n";
	        
	        // add now the outer colors;
	        texString += "\\filldraw[even odd rule,fill=owlClassColor,line width=1pt] (" + leftPos + "pt, " + py + "pt) circle (12.5pt)  (" + rightPos + "pt, " + py + "pt) circle (12.5pt);\n ";
	        
	        // add texts
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + py + "pt)  (intersectionText" + i + ") {$\\cap$};\n";
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + px + "pt, " + txtOffset + "pt)   (Node_text" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	        
	      }
	      
	      
	      i++;
	      
	    });
	    for ( i = 0; i < propElements.length; i++ ) {
	      var correspondingProp = propElements[i].property();
	      var p_px = propElements[i].x;
	      var p_py = -propElements[i].y;
	      identifier = correspondingProp.labelForCurrentLanguage();
	      if ( identifier === undefined ) identifier = "";
	      var textColorStr = "";
	      if ( correspondingProp.textBlock && correspondingProp.textBlock() ) {
	        var txtColor = correspondingProp.textBlock()._textBlock().style("fill");
	        //  console.log("PropertyTextColor="+txtColor);
	        if ( txtColor === "rgb(0, 0, 0)" ) {
	          textColorStr = ", text=black";
	        }
	        if ( txtColor === "rgb(255, 255, 255)" ) {
	          textColorStr = ", text=white";
	        }
	        var tspans = correspondingProp.textBlock()._textBlock().node().children;
	        
	        // identifier=node.textBlock()._textBlock().text();
	        // console.log(tspans);
	        if ( tspans[0] ) {
	          identifier = tspans[0].innerHTML;
	          
	          for ( var t = 1; t < tspans.length; t++ ) {
	            var spanText = tspans[t].innerHTML;
	            if ( spanText.indexOf("(") > -1 ) {
	              identifier += "\\\\ {\\small " + tspans[t].innerHTML + " }";
	            }
	            else {
	              identifier += "\\\\ " + tspans[t].innerHTML;
	            }
	          }
	        }
	        else {
	        }
	      }
	      if ( correspondingProp.type() === "setOperatorProperty" ) {
	        continue; // this property does not have a label
	      }
	      var qType = "owlObjectProperty";
	      if ( correspondingProp.type() === "owl:DatatypeProperty" ) {
	        qType = "owlDatatypeProperty";
	      }
	      if ( correspondingProp.type() === "rdfs:subClassOf" ) {
	        qType = "rdfsSubClassOf";
	      }
	      if ( correspondingProp.type() === "rdf:Property" ) {
	        qType = "rdfProperty";
	      }
	      
	      
	      var bgColorStr = "";
	      if ( correspondingProp.backgroundColor() ) {
	        // console.log("Found backGround color");
	        var bgColor = correspondingProp.backgroundColor();
	        //console.log(bgColor);
	        bgColor.toUpperCase();
	        bgColor = bgColor.slice(1, bgColor.length);
	        texString += "\\definecolor{property" + i + "_COLOR}{HTML}{" + bgColor + "} \n ";
	        bgColorStr = ", fill=property" + i + "_COLOR ";
	      }
	      if ( correspondingProp.attributes().indexOf("deprecated") > -1 ) {
	        texString += "\\definecolor{property" + i + "_COLOR}{HTML}{CCCCCC} \n ";
	        bgColorStr = ", fill=property" + i + "_COLOR ";
	      }
	      
	      var widthString = "";
	      var width = correspondingProp.textWidth();
	      widthString = ",minimum width=" + width + "pt";
	      
	      
	      // OWL INTERSECTION OF
	      if ( correspondingProp.type() === "owl:disjointWith" ) {
	        var leftPos = p_px - 12;
	        var rightPos = p_px + 12;
	        var txtOffset = p_py - 20;
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + p_px + "pt, " + p_py + "pt)   (Node" + i + ") {};\n";
	        texString += "\\node[disjointWith , text=black] at (" + leftPos + "pt, " + p_py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[disjointWith , text=black] at (" + rightPos + "pt, " + p_py + "pt)   (SymbolNode" + i + ") {};\n";
	        texString += "\\node[font={\\fontsize{12pt}{12}\\selectfont \\sffamily }" + textColorStr + "] at (" + p_px + "pt, " + txtOffset + "pt)   (Node_text" + i + ") {";
	        if ( graph.options().compactNotation() === false ) {
	          texString += "(disjoint)";
	        }
	        texString += "};\n";
	        continue;
	      }
	      
	      
	      if ( correspondingProp.inverse() ) {
	        var inv_correspondingProp = correspondingProp.inverse();
	        // create the rendering element for the inverse property;
	        var inv_identifier = inv_correspondingProp.labelForCurrentLanguage();
	        if ( inv_identifier === undefined ) inv_identifier = "";
	        var inv_textColorStr = "";
	        //console.log(inv_correspondingProp);
	        //console.log(inv_correspondingProp.textBlock());
	        
	        if ( inv_correspondingProp.textBlock && inv_correspondingProp.textBlock() ) {
	          
	          var inv_txtColor = inv_correspondingProp.textBlock()._textBlock().style("fill");
	          //  console.log("PropertyTextColor="+inv_txtColor);
	          if ( inv_txtColor === "rgb(0, 0, 0)" ) {
	            inv_textColorStr = ", text=black";
	          }
	          if ( inv_txtColor === "rgb(255, 255, 255)" ) {
	            inv_textColorStr = ", text=white";
	          }
	          var inv_tspans = inv_correspondingProp.textBlock()._textBlock().node().children;
	          
	          // identifier=node.textBlock()._textBlock().text();
	          //  console.log(inv_tspans);
	          if ( inv_tspans[0] ) {
	            inv_identifier = inv_tspans[0].innerHTML;
	            
	            for ( var inv_t = 1; inv_t < inv_tspans.length; inv_t++ ) {
	              var ispanText = inv_tspans[inv_t].innerHTML;
	              if ( ispanText.indexOf("(") > -1 ) {
	                inv_identifier += "\\\\ {\\small " + inv_tspans[inv_t].innerHTML + " }";
	              } else {
	                inv_identifier += "\\\\ " + inv_tspans[inv_t].innerHTML;
	              }
	            }
	          }
	        }
	        var inv_qType = "owlObjectProperty";
	        var inv_bgColorStr = "";
	        
	        if ( inv_correspondingProp.backgroundColor() ) {
	          //  console.log("Found backGround color");
	          var inv_bgColor = inv_correspondingProp.backgroundColor();
	          //   console.log(inv_bgColor);
	          inv_bgColor.toUpperCase();
	          inv_bgColor = inv_bgColor.slice(1, inv_bgColor.length);
	          texString += "\\definecolor{inv_property" + i + "_COLOR}{HTML}{" + inv_bgColor + "} \n ";
	          inv_bgColorStr = ", fill=inv_property" + i + "_COLOR ";
	        }
	        if ( inv_correspondingProp.attributes().indexOf("deprecated") > -1 ) {
	          texString += "\\definecolor{inv_property" + i + "_COLOR}{HTML}{CCCCCC} \n ";
	          inv_bgColorStr = ", fill=inv_property" + i + "_COLOR ";
	        }
	        
	        var inv_widthString = "";
	        var inv_width = inv_correspondingProp.textWidth();
	        
	        var pOY1 = p_py - 14;
	        var pOY2 = p_py + 14;
	        inv_widthString = ",minimum width=" + inv_width + "pt";
	        texString += "% Createing Inverse Property \n";
	        texString += "\\node[" + inv_qType + " " + inv_widthString + " " + inv_bgColorStr + " " + inv_textColorStr + "] at (" + p_px + "pt, " + pOY1 + "pt)   (property" + i + ") {" + inv_identifier.replaceAll("_", "\\_ ") + "};\n";
	        texString += "% " + inv_qType + " vs " + qType + "\n";
	        texString += "% " + inv_widthString + " vs " + widthString + "\n";
	        texString += "% " + inv_bgColorStr + " vs " + bgColorStr + "\n";
	        texString += "% " + inv_textColorStr + " vs " + textColorStr + "\n";
	        
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + p_px + "pt, " + pOY2 + "pt)   (property" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	        
	      } else {
	        texString += "\\node[" + qType + " " + widthString + " " + bgColorStr + " " + textColorStr + "] at (" + p_px + "pt, " + p_py + "pt)   (property" + i + ") {" + identifier.replaceAll("_", "\\_ ") + "};\n";
	      }
	    }
	    
	    texString += "\\end{tikzpicture}\n}\n \\end{center}\n";
	    
	    //   console.log("Tex Output\n"+ texString);
	    var dataURI = "data:text/json;charset=utf-8," + encodeURIComponent(texString);
	    exportTexButton.attr("href", dataURI)
	      .attr("download", exportFilename + ".tex");
	    
	    
	  }
	  
	  function calculateRadian( angle ){
	    angle = angle % 360;
	    if ( angle < 0 ) {
	      angle = angle + 360;
	    }
	    return (Math.PI * angle) / 180;
	  }
	  
	  function calculateAngle( radian ){
	    return radian * (180 / Math.PI);
	  }
	  
	  return exportMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 326:
/***/ (function(module, exports) {

	/**
	 * Contains the logic for the export button.
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  var exportTTLModule = {};
	  var resultingTTLContent = "";
	  var currentNodes;
	  var currentProperties;
	  var currentAxioms;
	  var Map_ID2Node = {};
	  var Map_ID2Prop = {};
	  var prefixModule = webvowl.util.prefixTools(graph);
	  
	  exportTTLModule.requestExport = function (){
	    prefixModule.updatePrefixModel();
	    resultingTTLContent = "";
	    currentNodes = graph.getClassDataForTtlExport();
	    var i;
	    for ( i = 0; i < currentNodes.length; i++ ) {
	      Map_ID2Node[currentNodes[i].id()] = currentNodes[i];
	    }
	    currentProperties = graph.getPropertyDataForTtlExport();
	    
	    for ( i = 0; i < currentProperties.length; i++ ) {
	      Map_ID2Prop[currentProperties[i].id()] = currentProperties[i];
	    }
	    
	    
	    prepareHeader();
	    preparePrefixList();
	    prepareOntologyDef();
	    resultingTTLContent += "#################################################################\r\n\r\n";
	    preparePrefixRepresentation();
	    var property_success = exportProperties();
	    var class_success = exportClasses();
	    currentNodes = null;
	    currentProperties = null;
	    Map_ID2Node = {};
	    Map_ID2Prop = {};
	    if ( property_success === false || class_success === false )
	      return false;
	    return true;
	    
	  };
	  
	  function preparePrefixRepresentation(){
	    var i;
	    var allNodes = graph.getUnfilteredData().nodes;
	    var allProps = graph.getUnfilteredData().properties;
	    for ( i = 0; i < allNodes.length; i++ ) {
	      var nodeIRI = prefixModule.getPrefixRepresentationForFullURI(allNodes[i].iri());
	      if ( prefixModule.validURL(nodeIRI) === true )
	        allNodes[i].prefixRepresentation = "<" + nodeIRI + ">";
	      else
	        allNodes[i].prefixRepresentation = nodeIRI;
	    }
	    for ( i = 0; i < allProps.length; i++ ) {
	      var propIRI = prefixModule.getPrefixRepresentationForFullURI(allProps[i].iri());
	      if ( prefixModule.validURL(propIRI) === true )
	        allProps[i].prefixRepresentation = "<" + propIRI + ">";
	      else
	        allProps[i].prefixRepresentation = propIRI;
	    }
	  }
	  
	  function exportProperties(){
	    if ( currentProperties.length === 0 ) return; // we dont need to write that
	    resultingTTLContent += "###  Property Definitions (Number of Property) " + currentProperties.length + " ###\r\n";
	    for ( var i = 0; i < currentProperties.length; i++ ) {
	      
	      resultingTTLContent += "#  --------------------------- Property " + i + "------------------------- \r\n";
	      var addedElement = extractPropertyDescription(currentProperties[i]);
	      resultingTTLContent += addedElement;
	      //@ workaround for not supported elements
	      if ( addedElement.indexOf("WHYEMPTYNAME") !== -1 ) {
	        return false;
	      }
	    }
	    return true;
	  }
	  
	  
	  function exportClasses(){
	    if ( currentNodes.length === 0 ) return; // we dont need to write that
	    resultingTTLContent += "###  Class Definitions (Number of Classes) " + currentNodes.length + " ###\r\n";
	    for ( var i = 0; i < currentNodes.length; i++ ) {
	      // check for node type here and return false
	      resultingTTLContent += "#  --------------------------- Class  " + i + "------------------------- \r\n";
	      var addedElement = extractClassDescription(currentNodes[i]);
	      resultingTTLContent += addedElement;
	      
	      if ( addedElement.indexOf("WHYEMPTYNAME") !== -1 ) {
	        return false;
	      }
	    }
	    return true;
	  }
	  
	  function getPresentAttribute( selectedElement, element ){
	    var attr = selectedElement.attributes();
	    return (attr.indexOf(element) >= 0);
	  }
	  
	  function extractClassDescription( node ){
	    var subject = node.prefixRepresentation;
	    var predicate = "rdf:type";
	    var object = node.type();
	    if ( node.type() === "owl:equivalentClass" )
	      object = "owl:Class";
	    if ( node.type() === "owl:disjointUnionOf" )
	      object = "owl:Class";
	    if ( node.type() === "owl:unionOf" )
	      object = "owl:Class";
	    var arrayOfNodes = [];
	    var arrayOfUnionNodes = [];
	    
	    if ( node.union() ) {
	      var union = node.union();
	      for ( var u = 0; u < union.length; u++ ) {
	        var u_node = Map_ID2Node[union[u]];
	        arrayOfUnionNodes.push(u_node);
	      }
	    }
	    
	    if ( node.disjointUnion() ) {
	      var distUnion = node.disjointUnion();
	      for ( var du = 0; du < distUnion.length; du++ ) {
	        var du_node = Map_ID2Node[distUnion[du]];
	        arrayOfNodes.push(du_node);
	      }
	    }
	    
	    var objectDef = subject + " " + predicate + " " + object;
	    if ( getPresentAttribute(node, "deprecated") === true ) {
	      objectDef += ", owl:DeprecatedProperty";
	    }
	    // equivalent class handeled using type itself!
	    
	    // check for equivalent classes;
	    var indent = getIndent(subject);
	    objectDef += "; \r\n";
	    for ( var e = 0; e < node.equivalents().length; e++ ) {
	      var eqIRI = prefixModule.getPrefixRepresentationForFullURI(node.equivalents()[e].iri());
	      var eqNode_prefRepresentation = "";
	      if ( prefixModule.validURL(eqIRI) === true )
	        eqNode_prefRepresentation = "<" + eqIRI + ">";
	      else
	        eqNode_prefRepresentation = eqIRI;
	      objectDef += indent + " owl:equivalentClass " + eqNode_prefRepresentation + " ;\r\n";
	    }
	    
	    // if (getPresentAttribute(node,"equivalent")===true){
	    //     objectDef+=", owl:EquivalentClass";
	    // }
	    
	    // add Comments
	    
	    if ( node.commentForCurrentLanguage() ) {
	      
	      objectDef += indent + " rdfs:comment \"" + node.commentForCurrentLanguage() + "\" ;\r\n";
	    }
	    
	    if ( node.annotations() ) {
	      var annotations = node.annotations();
	      for ( var an in annotations ) {
	        if ( annotations.hasOwnProperty(an) ) {
	          var anArrayObj = annotations[an];
	          var anObj = anArrayObj[0];
	          var an_ident = anObj.identifier;
	          var an_val = anObj.value;
	          
	          if ( an_ident === "isDefinedBy" ) {
	            objectDef += indent + " rdfs:isDefinedBy <" + an_val + "> ;\r\n";
	          }
	          if ( an_ident === "term_status" ) {
	            objectDef += indent + " vs:term_status \"" + an_val + "\" ;\r\n";
	          }
	        }
	      }
	    }
	    
	    
	    if ( arrayOfNodes.length > 0 ) {
	      // add disjoint unionOf
	      objectDef += indent + " owl:disjointUnionOf (";
	      for ( var duE = 0; duE < arrayOfNodes.length; duE++ ) {
	        var duIri = prefixModule.getPrefixRepresentationForFullURI(arrayOfNodes[duE].iri());
	        var duNode_prefRepresentation = "";
	        if ( prefixModule.validURL(duIri) === true )
	          duNode_prefRepresentation = "<" + duIri + ">";
	        else
	          duNode_prefRepresentation = duIri;
	        objectDef += indent + indent + duNode_prefRepresentation + " \n";
	      }
	      objectDef += ") ;\r\n";
	    }
	    
	    if ( arrayOfUnionNodes.length > 0 ) {
	      // add disjoint unionOf
	      objectDef += indent + " rdfs:subClassOf [ rdf:type owl:Class ; \r\n";
	      objectDef += indent + indent + " owl:unionOf ( ";
	      
	      for ( var uE = 0; uE < arrayOfUnionNodes.length; uE++ ) {
	        
	        if ( arrayOfUnionNodes[uE] && arrayOfUnionNodes[uE].iri() ) {
	          var uIri = prefixModule.getPrefixRepresentationForFullURI(arrayOfUnionNodes[uE].iri());
	          var uNode_prefRepresentation = "";
	          if ( prefixModule.validURL(uIri) === true )
	            uNode_prefRepresentation = "<" + uIri + ">";
	          else
	            uNode_prefRepresentation = uIri;
	          objectDef += indent + indent + indent + uNode_prefRepresentation + " \n";
	        }
	      }
	      objectDef += ") ;\r\n";
	      
	      
	    }
	    
	    
	    var allProps = graph.getUnfilteredData().properties;
	    var myProperties = [];
	    var i;
	    for ( i = 0; i < allProps.length; i++ ) {
	      if ( allProps[i].domain() === node &&
	        (   allProps[i].type() === "rdfs:subClassOf" ||
	        allProps[i].type() === "owl:allValuesFrom" ||
	        allProps[i].type() === "owl:someValuesFrom")
	      ) {
	        myProperties.push(allProps[i]);
	      }
	      // special case disjoint with>> both domain and range get that property
	      if ( (allProps[i].domain() === node) &&
	        allProps[i].type() === "owl:disjointWith" ) {
	        myProperties.push(allProps[i]);
	      }
	      
	    }
	    for ( i = 0; i < myProperties.length; i++ ) {
	      // depending on the property we have to do some things;
	      
	      // special case
	      if ( myProperties[i].type() === "owl:someValuesFrom" ) {
	        objectDef += indent + " rdfs:subClassOf [ rdf:type owl:Restriction ; \r\n";
	        objectDef += indent + "                   owl:onProperty " + myProperties[i].prefixRepresentation + ";\r\n";
	        if ( myProperties[i].range().type() !== "owl:Thing" ) {
	          objectDef += indent + "                   owl:someValuesFrom " + myProperties[i].range().prefixRepresentation + "\r\n";
	        }
	        objectDef += indent + "                 ];\r\n";
	        continue;
	      }
	      
	      if ( myProperties[i].type() === "owl:allValuesFrom" ) {
	        objectDef += indent + " rdfs:subClassOf [ rdf:type owl:Restriction ; \r\n";
	        objectDef += indent + "                   owl:onProperty " + myProperties[i].prefixRepresentation + ";\r\n";
	        if ( myProperties[i].range().type() !== "owl:Thing" ) {
	          objectDef += indent + "                   owl:allValuesFrom " + myProperties[i].range().prefixRepresentation + "\r\n";
	        }
	        objectDef += indent + "                 ];\r\n";
	        continue;
	      }
	      
	      if ( myProperties[i].range().type() !== "owl:Thing" ) {
	        objectDef += indent + " " + myProperties[i].prefixRepresentation +
	          " " + myProperties[i].range().prefixRepresentation + " ;\r\n";
	        
	        
	      }
	    }
	    
	    
	    objectDef += general_Label_languageExtractor(indent, node.label(), "rdfs:label", true);
	    return objectDef;
	    
	  }
	  
	  function extractPropertyDescription( property ){
	    var subject = property.prefixRepresentation;
	    if ( subject.length === 0 ) {
	      console.log("THIS SHOULD NOT HAPPEN");
	      var propIRI = prefixModule.getPrefixRepresentationForFullURI(property.iri());
	      console.log("FOUND " + propIRI);
	      
	      
	    }
	    var predicate = "rdf:type";
	    var object = property.type();
	    
	    var objectDef = subject + " " + predicate + " " + object;
	    if ( getPresentAttribute(property, "deprecated") === true ) {
	      objectDef += ", owl:DeprecatedProperty";
	    }
	    if ( getPresentAttribute(property, "functional") === true ) {
	      objectDef += ", owl:FunctionalProperty";
	    }
	    if ( getPresentAttribute(property, "inverse functional") === true ) {
	      objectDef += ", owl:InverseFunctionalProperty";
	    }
	    if ( getPresentAttribute(property, "symmetric") === true ) {
	      objectDef += ", owl:SymmetricProperty";
	    }
	    if ( getPresentAttribute(property, "transitive") === true ) {
	      objectDef += ", owl:TransitiveProperty";
	    }
	    var indent = getIndent(subject);
	    
	    if ( property.inverse() ) {
	      objectDef += "; \r\n";
	      objectDef += indent + " owl:inverseOf " + property.inverse().prefixRepresentation;
	    }
	    
	    // check for domain and range;
	    
	    
	    var closeStatement = false;
	    var domain = property.domain();
	    var range = property.range();
	    
	    
	    objectDef += " ;\r\n";
	    
	    
	    if ( property.commentForCurrentLanguage() ) {
	      
	      objectDef += indent + " rdfs:comment \"" + property.commentForCurrentLanguage() + "\" ;\r\n";
	    }
	    
	    if ( property.superproperties() ) {
	      var superProps = property.superproperties();
	      for ( var sP = 0; sP < superProps.length; sP++ ) {
	        var sPelement = superProps[sP];
	        objectDef += indent + "rdfs:subPropertyOf " + sPelement.prefixRepresentation + ";\r\n";
	      }
	      // for (var an in annotations){
	      //     if (annotations.hasOwnProperty(an)){
	      //         var anArrayObj=annotations[an];
	      //         var anObj=anArrayObj[0];
	      //         var an_ident=anObj.identifier;
	      //         var an_val=anObj.value;
	      //         console.log(an_ident + " "+ an_val);
	      //
	      //         if (an_ident==="isDefinedBy"){
	      //             objectDef+=indent+" rdfs:isDefinedBy <"+an_val+"> ;\r\n";
	      //         }
	      //         if (an_ident==="term_status"){
	      //             objectDef+=indent+" vs:term_status \""+an_val+"\" ;\r\n";
	      //         }
	      //     }
	      // }
	      
	    }
	    
	    if ( property.annotations() ) {
	      var annotations = property.annotations();
	      for ( var an in annotations ) {
	        if ( annotations.hasOwnProperty(an) ) {
	          var anArrayObj = annotations[an];
	          var anObj = anArrayObj[0];
	          var an_ident = anObj.identifier;
	          var an_val = anObj.value;
	          
	          if ( an_ident === "isDefinedBy" ) {
	            objectDef += indent + " rdfs:isDefinedBy <" + an_val + "> ;\r\n";
	          }
	          if ( an_ident === "term_status" ) {
	            objectDef += indent + " vs:term_status \"" + an_val + "\" ;\r\n";
	          }
	        }
	      }
	    }
	    
	    
	    if ( domain.type() === "owl:Thing" && range.type() === "owl:Thing" ) {
	      // we do not write domain and range
	      if ( typeof property.label() !== "object" && property.label().length === 0 ) {
	        closeStatement = true;
	      }
	    }
	    
	    
	    if ( closeStatement === true ) {
	      var uobjectDef = objectDef.substring(0, objectDef.length - 2);
	      objectDef = uobjectDef + " . \r\n";
	      return objectDef;
	    }
	    // objectDef+="; \r\n";
	    var labelDescription;
	    
	    
	    if ( domain.type() === "owl:Thing" && range.type() === "owl:Thing" ) {
	      labelDescription = general_Label_languageExtractor(indent, property.label(), "rdfs:label", true);
	      objectDef += labelDescription;
	    }
	    else {
	      // do not close the statement;
	      labelDescription = general_Label_languageExtractor(indent, property.label(), "rdfs:label");
	      objectDef += labelDescription;
	      if ( domain.type() !== "owl:Thing" ) {
	        objectDef += indent + " rdfs:domain " + domain.prefixRepresentation + ";\r\n";
	      }
	      if ( range.type() !== "owl:Thing" ) {
	        objectDef += indent + " rdfs:range " + range.prefixRepresentation + ";\r\n";
	      }
	      
	      // close statement now;
	      
	      var s_needUpdate = objectDef;
	      var s_lastPtr = s_needUpdate.lastIndexOf(";");
	      objectDef = s_needUpdate.substring(0, s_lastPtr) + " . \r\n";
	    }
	    
	    return objectDef;
	    
	  }
	  
	  
	  exportTTLModule.resultingTTL_Content = function (){
	    return resultingTTLContent;
	  };
	  
	  function getIndent( name ){
	    if ( name === undefined ) {
	      return "WHYEMPTYNAME?";
	    }
	    return new Array(name.length + 1).join(" ");
	  }
	  
	  function prepareHeader(){
	    resultingTTLContent += "#################################################################\r\n";
	    resultingTTLContent += "###  Generated with the experimental alpha version of the TTL exporter of WebVOWL (version 1.1.3) " +
	      " http://visualdataweb.de/webvowl/   ###\r\n";
	    resultingTTLContent += "#################################################################\r\n\r\n";
	    
	  }
	  
	  function preparePrefixList(){
	    var ontoIri = graph.options().getGeneralMetaObjectProperty('iri');
	    var prefixList = graph.options().prefixList();
	    var prefixDef = [];
	    prefixDef.push('@prefix : \t\t<' + ontoIri + '> .');
	    for ( var name in prefixList ) {
	      if ( prefixList.hasOwnProperty(name) ) {
	        prefixDef.push('@prefix ' + name + ': \t\t<' + prefixList[name] + '> .');
	      }
	    }
	    prefixDef.push('@base \t\t\t<' + ontoIri + '> .\r\n');
	    
	    for ( var i = 0; i < prefixDef.length; i++ ) {
	      resultingTTLContent += prefixDef[i] + '\r\n';
	    }
	  }
	  
	  function prepareOntologyDef(){
	    var ontoIri = graph.options().getGeneralMetaObjectProperty('iri');
	    var indent = getIndent('<' + ontoIri + '>');
	    resultingTTLContent += '<' + ontoIri + '> rdf:type owl:Ontology ;\r\n' +
	      getOntologyTitle(indent) +
	      getOntologyDescription(indent) +
	      getOntologyVersion(indent) +
	      getOntologyAuthor(indent);
	    
	    // close the statement;
	    var s_needUpdate = resultingTTLContent;
	    var s_lastPtr = s_needUpdate.lastIndexOf(";");
	    resultingTTLContent = s_needUpdate.substring(0, s_lastPtr) + " . \r\n";
	  }
	  
	  function getOntologyTitle( indent ){
	    return general_languageExtractor(indent, "title", "dc:title");
	  }
	  
	  function getOntologyDescription( indent ){
	    return general_languageExtractor(indent, "description", "dc:description");
	  }
	  
	  function getOntologyAuthor( indent ){
	    var languageElement = graph.options().getGeneralMetaObjectProperty('author');
	    if ( languageElement ) {
	      if ( typeof languageElement !== "object" ) {
	        if ( languageElement.length === 0 )
	          return ""; // an empty string
	        var aString = indent + " dc:creator " + '"' + languageElement + '";\r\n';
	        return aString;
	      }
	      // we assume this thing is an array;
	      var authorString = indent + " dc:creator " + '"';
	      for ( var i = 0; i < languageElement.length - 1; i++ ) {
	        authorString += languageElement[i] + ", ";
	      }
	      authorString += languageElement[languageElement.length - 1] + '";\r\n';
	      return authorString;
	    } else {
	      return ""; // an empty string
	    }
	  }
	  
	  function getOntologyVersion( indent ){
	    var languageElement = graph.options().getGeneralMetaObjectProperty('version');
	    if ( languageElement ) {
	      if ( typeof languageElement !== "object" ) {
	        if ( languageElement.length === 0 )
	          return ""; // an empty string
	      }
	      return general_languageExtractor(indent, "version", "owl:versionInfo");
	    } else return ""; // an empty string
	  }
	  
	  function general_languageExtractor( indent, metaObjectDescription, annotationDescription, endStatement ){
	    var languageElement = graph.options().getGeneralMetaObjectProperty(metaObjectDescription);
	    
	    if ( typeof languageElement === 'object' ) {
	      
	      var resultingLanguages = [];
	      for ( var name in languageElement ) {
	        if ( languageElement.hasOwnProperty(name) ) {
	          var content = languageElement[name];
	          if ( name === "undefined" ) {
	            resultingLanguages.push(indent + " " + annotationDescription + ' "' + content + '"@en; \r\n');
	          }
	          else {
	            resultingLanguages.push(indent + " " + annotationDescription + ' "' + content + '"@' + name + '; \r\n');
	          }
	        }
	      }
	      // create resulting titles;
	      
	      var resultingString = "";
	      for ( var i = 0; i < resultingLanguages.length; i++ ) {
	        resultingString += resultingLanguages[i];
	      }
	      if ( endStatement && endStatement === true ) {
	        var needUpdate = resultingString;
	        var lastPtr = needUpdate.lastIndexOf(";");
	        return needUpdate.substring(0, lastPtr) + ". \r\n";
	      } else {
	        return resultingString;
	      }
	      
	    } else {
	      if ( endStatement && endStatement === true ) {
	        var s_needUpdate = indent + " " + annotationDescription + ' "' + languageElement + '"@en; \r\n';
	        var s_lastPtr = s_needUpdate.lastIndexOf(";");
	        return s_needUpdate.substring(0, s_lastPtr) + " . \r\n";
	      }
	      return indent + " " + annotationDescription + ' "' + languageElement + '"@en;\r\n';
	    }
	  }
	  
	  function general_Label_languageExtractor( indent, label, annotationDescription, endStatement ){
	    var languageElement = label;
	    
	    if ( typeof languageElement === 'object' ) {
	      var resultingLanguages = [];
	      for ( var name in languageElement ) {
	        if ( languageElement.hasOwnProperty(name) ) {
	          var content = languageElement[name];
	          if ( name === "undefined" ) {
	            resultingLanguages.push(indent + " " + annotationDescription + ' "' + content + '"@en; \r\n');
	          }
	          else {
	            resultingLanguages.push(indent + " " + annotationDescription + ' "' + content + '"@' + name + '; \r\n');
	          }
	        }
	      }
	      // create resulting titles;
	      var resultingString = "";
	      for ( var i = 0; i < resultingLanguages.length; i++ ) {
	        resultingString += resultingLanguages[i];
	      }
	      if ( endStatement && endStatement === true ) {
	        var needUpdate = resultingString;
	        var lastPtr = needUpdate.lastIndexOf(";");
	        return needUpdate.substring(0, lastPtr) + " . \r\n";
	      } else {
	        return resultingString;
	      }
	      
	    } else {
	      if ( endStatement && endStatement === true ) {
	        var s_needUpdate = indent + " " + annotationDescription + ' "' + languageElement + '"@en; \r\n';
	        var s_lastPtr = s_needUpdate.lastIndexOf(";");
	        return s_needUpdate.substring(0, s_lastPtr) + " . \r\n";
	      }
	      return indent + " " + annotationDescription + ' "' + languageElement + '"@en; \r\n';
	    }
	  }
	  
	  return exportTTLModule;
	};


/***/ }),

/***/ 327:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the logic for connecting the filters with the website.
	 *
	 * @param graph required for calling a refresh after a filter change
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  
	  var filterMenu = {},
	    checkboxData = [],
	    menuElement = d3.select("#m_filter"),
	    menuControl = d3.select("#c_filter a"),
	    nodeDegreeContainer = d3.select("#nodeDegreeFilteringOption"),
	    graphDegreeLevel,
	    defaultDegreeValue = 0,
	    degreeSlider;
	  
	  filterMenu.setDefaultDegreeValue = function ( val ){
	    defaultDegreeValue = val;
	  };
	  filterMenu.getDefaultDegreeValue = function (){
	    return defaultDegreeValue;
	  };
	  
	  filterMenu.getGraphObject = function (){
	    return graph;
	  };
	  /** some getter function  **/
	  filterMenu.getCheckBoxContainer = function (){
	    return checkboxData;
	  };
	  
	  filterMenu.getDegreeSliderValue = function (){
	    return degreeSlider.property("value");
	  };
	  /**
	   * Connects the website with graph filters.
	   * @param datatypeFilter filter for all datatypes
	   * @param objectPropertyFilter filter for all object properties
	   * @param subclassFilter filter for all subclasses
	   * @param disjointFilter filter for all disjoint with properties
	   * @param setOperatorFilter filter for all set operators with properties
	   * @param nodeDegreeFilter filters nodes by their degree
	   */
	  filterMenu.setup = function ( datatypeFilter, objectPropertyFilter, subclassFilter, disjointFilter, setOperatorFilter, nodeDegreeFilter ){
	    // TODO: is this here really necessarry? << new menu visualization style?
	    menuControl.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	    menuControl.on("mouseleave", function (){
	      filterMenu.highlightForDegreeSlider(false);
	    });
	    
	    addFilterItem(datatypeFilter, "datatype", "Datatype properties", "#datatypeFilteringOption");
	    addFilterItem(objectPropertyFilter, "objectProperty", "Object properties", "#objectPropertyFilteringOption");
	    addFilterItem(subclassFilter, "subclass", "Solitary subclasses", "#subclassFilteringOption");
	    addFilterItem(disjointFilter, "disjoint", "Class disjointness", "#disjointFilteringOption");
	    addFilterItem(setOperatorFilter, "setoperator", "Set operators", "#setOperatorFilteringOption");
	    
	    addNodeDegreeFilter(nodeDegreeFilter, nodeDegreeContainer);
	    addAnimationFinishedListener();
	  };
	  
	  
	  function addFilterItem( filter, identifier, pluralNameOfFilteredItems, selector ){
	    var filterContainer,
	      filterCheckbox;
	    
	    filterContainer = d3.select(selector)
	      .append("div")
	      .classed("checkboxContainer", true);
	    
	    filterCheckbox = filterContainer.append("input")
	      .classed("filterCheckbox", true)
	      .attr("id", identifier + "FilterCheckbox")
	      .attr("type", "checkbox")
	      .property("checked", filter.enabled());
	    
	    // Store for easier resetting
	    checkboxData.push({ checkbox: filterCheckbox, defaultState: filter.enabled() });
	    
	    filterCheckbox.on("click", function ( silent ){
	      // There might be no parameters passed because of a manual
	      // invocation when resetting the filters
	      var isEnabled = filterCheckbox.property("checked");
	      filter.enabled(isEnabled);
	      if ( silent !== true ) {
	        // updating graph when silent is false or the parameter is not given.
	        graph.update();
	      }
	    });
	    
	    filterContainer.append("label")
	      .attr("for", identifier + "FilterCheckbox")
	      .text(pluralNameOfFilteredItems);
	  }
	  
	  function addNodeDegreeFilter( nodeDegreeFilter, container ){
	    nodeDegreeFilter.setMaxDegreeSetter(function ( maxDegree ){
	      degreeSlider.attr("max", maxDegree);
	      setSliderValue(degreeSlider, Math.min(maxDegree, degreeSlider.property("value")));
	    });
	    
	    nodeDegreeFilter.setDegreeGetter(function (){
	      return degreeSlider.property("value");
	    });
	    
	    nodeDegreeFilter.setDegreeSetter(function ( value ){
	      setSliderValue(degreeSlider, value);
	    });
	    
	    var sliderContainer,
	      sliderValueLabel;
	    
	    sliderContainer = container.append("div")
	      .classed("distanceSliderContainer", true);
	    
	    degreeSlider = sliderContainer.append("input")
	      .attr("id", "nodeDegreeDistanceSlider")
	      .attr("type", "range")
	      .attr("min", 0)
	      .attr("step", 1);
	    
	    sliderContainer.append("label")
	      .classed("description", true)
	      .attr("for", "nodeDegreeDistanceSlider")
	      .text("Degree of collapsing");
	    
	    sliderValueLabel = sliderContainer.append("label")
	      .classed("value", true)
	      .attr("for", "nodeDegreeDistanceSlider")
	      .text(0);
	    
	    
	    degreeSlider.on("change", function ( silent ){
	      if ( silent !== true ) {
	        graph.update();
	        graphDegreeLevel = degreeSlider.property("value");
	      }
	    });
	    
	    
	    degreeSlider.on("input", function (){
	      var degree = degreeSlider.property("value");
	      sliderValueLabel.text(degree);
	    });
	    
	    
	    // adding wheel events
	    degreeSlider.on("wheel", handleWheelEvent);
	    degreeSlider.on("focusout", function (){
	      if ( degreeSlider.property("value") !== graphDegreeLevel ) {
	        graph.update();
	      }
	    });
	  }
	  
	  function handleWheelEvent(){
	    var wheelEvent = d3.event;
	    
	    var offset;
	    if ( wheelEvent.deltaY < 0 ) offset = 1;
	    if ( wheelEvent.deltaY > 0 ) offset = -1;
	    var maxDeg = parseInt(degreeSlider.attr("max"));
	    var oldVal = parseInt(degreeSlider.property("value"));
	    var newSliderValue = oldVal + offset;
	    if ( oldVal !== newSliderValue && (newSliderValue >= 0 && newSliderValue <= maxDeg) ) {
	      // only update when they are different [reducing redundant updates]
	      // set the new value and emit an update signal
	      degreeSlider.property("value", newSliderValue);
	      degreeSlider.on("input")();// <<-- sets the text value
	      graph.update();
	    }
	    d3.event.preventDefault();
	  }
	  
	  function setSliderValue( slider, value ){
	    slider.property("value", value).on("input")();
	  }
	  
	  /**
	   * Resets the filters (and also filtered elements) to their default.
	   */
	  filterMenu.reset = function (){
	    checkboxData.forEach(function ( checkboxData ){
	      var checkbox = checkboxData.checkbox,
	        enabledByDefault = checkboxData.defaultState,
	        isChecked = checkbox.property("checked");
	      
	      if ( isChecked !== enabledByDefault ) {
	        checkbox.property("checked", enabledByDefault);
	        // Call onclick event handlers programmatically
	        checkbox.on("click")();
	      }
	    });
	    
	    setSliderValue(degreeSlider, 0);
	    degreeSlider.on("change")();
	  };
	  
	  function addAnimationFinishedListener(){
	    menuControl.node().addEventListener("animationend", function (){
	      menuControl.classed("buttonPulse", false);
	      menuControl.classed("filterMenuButtonHighlight", true);
	      
	    });
	  }
	  
	  filterMenu.killButtonAnimation = function (){
	    menuControl.classed("buttonPulse", false);
	    menuControl.classed("filterMenuButtonHighlight", false);
	  };
	  
	  
	  filterMenu.highlightForDegreeSlider = function ( enable ){
	    if ( !arguments.length ) {
	      enable = true;
	    }
	    menuControl.classed("highlighted", enable);
	    nodeDegreeContainer.classed("highlighted", enable);
	    // pulse button handling
	    if ( menuControl.classed("buttonPulse") === true && enable === true ) {
	      menuControl.classed("buttonPulse", false);
	      var timer = setTimeout(function (){
	        menuControl.classed("buttonPulse", enable);
	        clearTimeout(timer);
	        // after the time is done, remove the pulse but stay highlighted
	      }, 100);
	    } else {
	      menuControl.classed("buttonPulse", enable);
	      menuControl.classed("filterMenuButtonHighlight", enable);
	    }
	  };
	  
	  
	  /** importer functions **/
	  // setting manually the values of the filter
	  // no update of the gui settings, these are updated in updateSettings
	  filterMenu.setCheckBoxValue = function ( id, checked ){
	    for ( var i = 0; i < checkboxData.length; i++ ) {
	      var cbdId = checkboxData[i].checkbox.attr("id");
	      if ( cbdId === id ) {
	        checkboxData[i].checkbox.property("checked", checked);
	        break;
	      }
	    }
	  };
	  
	  filterMenu.getCheckBoxValue = function ( id ){
	    for ( var i = 0; i < checkboxData.length; i++ ) {
	      var cbdId = checkboxData[i].checkbox.attr("id");
	      if ( cbdId === id ) {
	        return checkboxData[i].checkbox.property("checked");
	        
	      }
	    }
	  };
	  // set the value of the slider
	  filterMenu.setDegreeSliderValue = function ( val ){
	    degreeSlider.property("value", val);
	  };
	  
	  filterMenu.getDegreeSliderValue = function (){
	    return degreeSlider.property("value");
	  };
	  
	  // update the gui without invoking graph update (calling silent onclick function)
	  filterMenu.updateSettings = function (){
	    var silent = true;
	    var sliderValue = degreeSlider.property("value");
	    if ( sliderValue > 0 ) {
	      filterMenu.highlightForDegreeSlider(true);
	    } else {
	      filterMenu.highlightForDegreeSlider(false);
	    }
	    checkboxData.forEach(function ( checkboxData ){
	      var checkbox = checkboxData.checkbox;
	      checkbox.on("click")(silent);
	    });
	    
	    degreeSlider.on("input")();
	    degreeSlider.on("change")();
	    
	  };
	  
	  return filterMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 328:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the logic for setting up the gravity sliders.
	 *
	 * @param graph the associated webvowl graph
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  
	  var gravityMenu = {},
	    sliders = [],
	    options = graph.graphOptions(),
	    defaultCharge = options.charge();
	  
	  
	  /**
	   * Adds the gravity sliders to the website.
	   */
	  gravityMenu.setup = function (){
	    var menuEntry = d3.select("#m_gravity");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	    addDistanceSlider("#classSliderOption", "class", "Class distance", options.classDistance);
	    addDistanceSlider("#datatypeSliderOption", "datatype", "Datatype distance", options.datatypeDistance);
	  };
	  
	  function addDistanceSlider( selector, identifier, label, distanceFunction ){
	    var defaultLinkDistance = distanceFunction();
	    
	    var sliderContainer,
	      sliderValueLabel;
	    
	    sliderContainer = d3.select(selector)
	      .append("div")
	      .datum({ distanceFunction: distanceFunction }) // connect the options-function with the slider
	      .classed("distanceSliderContainer", true);
	    
	    var slider = sliderContainer.append("input")
	      .attr("id", identifier + "DistanceSlider")
	      .attr("type", "range")
	      .attr("min", 10)
	      .attr("max", 600)
	      .attr("value", distanceFunction())
	      .attr("step", 10);
	    
	    sliderContainer.append("label")
	      .classed("description", true)
	      .attr("for", identifier + "DistanceSlider")
	      .text(label);
	    
	    sliderValueLabel = sliderContainer.append("label")
	      .classed("value", true)
	      .attr("for", identifier + "DistanceSlider")
	      .text(distanceFunction());
	    
	    // Store slider for easier resetting
	    sliders.push(slider);
	    
	    slider.on("focusout", function (){
	      graph.updateStyle();
	    });
	    
	    slider.on("input", function (){
	      var distance = slider.property("value");
	      distanceFunction(distance);
	      adjustCharge(defaultLinkDistance);
	      sliderValueLabel.text(distance);
	      graph.updateStyle();
	    });
	    
	    // add wheel event to the slider
	    slider.on("wheel", function (){
	      var wheelEvent = d3.event;
	      var offset;
	      if ( wheelEvent.deltaY < 0 ) offset = 10;
	      if ( wheelEvent.deltaY > 0 ) offset = -10;
	      var oldVal = parseInt(slider.property("value"));
	      var newSliderValue = oldVal + offset;
	      if ( newSliderValue !== oldVal ) {
	        slider.property("value", newSliderValue);
	        distanceFunction(newSliderValue);
	        slider.on("input")(); // << set text and update the graphStyles
	      }
	      d3.event.preventDefault();
	    });
	  }
	  
	  function adjustCharge( defaultLinkDistance ){
	    var greaterDistance = Math.max(options.classDistance(), options.datatypeDistance()),
	      ratio = greaterDistance / defaultLinkDistance,
	      newCharge = defaultCharge * ratio;
	    
	    options.charge(newCharge);
	  }
	  
	  /**
	   * Resets the gravity sliders to their default.
	   */
	  gravityMenu.reset = function (){
	    sliders.forEach(function ( slider ){
	      slider.property("value", function ( d ){
	        // Simply reload the distance from the options
	        return d.distanceFunction();
	      });
	      slider.on("input")();
	    });
	  };
	  
	  
	  return gravityMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 329:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the logic for connecting the modes with the website.
	 *
	 * @param graph the graph that belongs to these controls
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  
	  var SAME_COLOR_MODE = { text: "Multicolor", type: "same" };
	  var GRADIENT_COLOR_MODE = { text: "Multicolor", type: "gradient" };
	  
	  var modeMenu = {},
	    checkboxes = [],
	    colorModeSwitch;
	  
	  var dynamicLabelWidthCheckBox;
	  // getter and setter for the state of color modes
	  modeMenu.colorModeState = function ( s ){
	    if ( !arguments.length ) return colorModeSwitch.datum().active;
	    colorModeSwitch.datum().active = s;
	    return modeMenu;
	  };
	  
	  
	  modeMenu.setDynamicLabelWidth = function ( val ){
	    dynamicLabelWidthCheckBox.property("checked", val);
	  };
	  // getter for checkboxes
	  modeMenu.getCheckBoxContainer = function (){
	    return checkboxes;
	  };
	  // getter for the color switch [needed? ]
	  modeMenu.colorModeSwitch = function (){
	    return colorModeSwitch;
	  };
	  
	  /**
	   * Connects the website with the available graph modes.
	   */
	  modeMenu.setup = function ( pickAndPin, nodeScaling, compactNotation, colorExternals ){
	    var menuEntry = d3.select("#m_modes");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	    addCheckBoxD("labelWidth", "Dynamic label width", "#dynamicLabelWidth", graph.options().dynamicLabelWidth, 1);
	    addCheckBox("editorMode", "Editing ", "#editMode", graph.editorMode);
	    addModeItem(pickAndPin, "pickandpin", "Pick & pin", "#pickAndPinOption", false);
	    addModeItem(nodeScaling, "nodescaling", "Node scaling", "#nodeScalingOption", true);
	    addModeItem(compactNotation, "compactnotation", "Compact notation", "#compactNotationOption", true);
	    var container = addModeItem(colorExternals, "colorexternals", "Color externals", "#colorExternalsOption", true);
	    colorModeSwitch = addExternalModeSelection(container, colorExternals);
	  };
	  function addCheckBoxD( identifier, modeName, selector, onChangeFunc, updateLvl ){
	    var moduleOptionContainer = d3.select(selector)
	      .append("div")
	      .classed("checkboxContainer", true);
	    
	    var moduleCheckbox = moduleOptionContainer.append("input")
	      .classed("moduleCheckbox", true)
	      .attr("id", identifier + "ModuleCheckbox")
	      .attr("type", "checkbox")
	      .property("checked", onChangeFunc());
	    
	    moduleCheckbox.on("click", function ( d ){
	      var isEnabled = moduleCheckbox.property("checked");
	      onChangeFunc(isEnabled);
	      d3.select("#maxLabelWidthSlider").node().disabled = !isEnabled;
	      d3.select("#maxLabelWidthvalueLabel").classed("disabledLabelForSlider", !isEnabled);
	      d3.select("#maxLabelWidthDescriptionLabel").classed("disabledLabelForSlider", !isEnabled);
	      
	      if ( updateLvl > 0 ) {
	        graph.animateDynamicLabelWidth();
	        // graph.lazyRefresh();
	      }
	    });
	    moduleOptionContainer.append("label")
	      .attr("for", identifier + "ModuleCheckbox")
	      .text(modeName);
	    if ( identifier === "editorMode" ) {
	      moduleOptionContainer.append("label")
	        .attr("style", "font-size:10px;padding-top:3px")
	        .text("(experimental)");
	    }
	    
	    dynamicLabelWidthCheckBox = moduleCheckbox;
	  }
	  
	  function addCheckBox( identifier, modeName, selector, onChangeFunc ){
	    var moduleOptionContainer = d3.select(selector)
	      .append("div")
	      .classed("checkboxContainer", true);
	    
	    var moduleCheckbox = moduleOptionContainer.append("input")
	      .classed("moduleCheckbox", true)
	      .attr("id", identifier + "ModuleCheckbox")
	      .attr("type", "checkbox")
	      .property("checked", onChangeFunc());
	    
	    moduleCheckbox.on("click", function ( d ){
	      var isEnabled = moduleCheckbox.property("checked");
	      onChangeFunc(isEnabled);
	      if ( isEnabled === true )
	        graph.showEditorHintIfNeeded();
	    });
	    moduleOptionContainer.append("label")
	      .attr("for", identifier + "ModuleCheckbox")
	      .text(modeName);
	    if ( identifier === "editorMode" ) {
	      moduleOptionContainer.append("label")
	        .attr("style", "font-size:10px;padding-top:3px")
	        .text(" (experimental)");
	    }
	  }
	  
	  function addModeItem( module, identifier, modeName, selector, updateGraphOnClick ){
	    var moduleOptionContainer,
	      moduleCheckbox;
	    
	    moduleOptionContainer = d3.select(selector)
	      .append("div")
	      .classed("checkboxContainer", true)
	      .datum({ module: module, defaultState: module.enabled() });
	    
	    moduleCheckbox = moduleOptionContainer.append("input")
	      .classed("moduleCheckbox", true)
	      .attr("id", identifier + "ModuleCheckbox")
	      .attr("type", "checkbox")
	      .property("checked", module.enabled());
	    
	    // Store for easier resetting all modes
	    checkboxes.push(moduleCheckbox);
	    
	    moduleCheckbox.on("click", function ( d, silent ){
	      var isEnabled = moduleCheckbox.property("checked");
	      d.module.enabled(isEnabled);
	      if ( updateGraphOnClick && silent !== true ) {
	        graph.executeColorExternalsModule();
	        graph.executeCompactNotationModule();
	        graph.lazyRefresh();
	      }
	    });
	    
	    moduleOptionContainer.append("label")
	      .attr("for", identifier + "ModuleCheckbox")
	      .text(modeName);
	    
	    return moduleOptionContainer;
	  }
	  
	  function addExternalModeSelection( container, colorExternalsMode ){
	    var button = container.append("button").datum({ active: false }).classed("color-mode-switch", true);
	    applyColorModeSwitchState(button, colorExternalsMode);
	    
	    button.on("click", function ( silent ){
	      var data = button.datum();
	      data.active = !data.active;
	      applyColorModeSwitchState(button, colorExternalsMode);
	      if ( colorExternalsMode.enabled() && silent !== true ) {
	        graph.executeColorExternalsModule();
	        graph.lazyRefresh();
	      }
	    });
	    
	    return button;
	  }
	  
	  function applyColorModeSwitchState( element, colorExternalsMode ){
	    var isActive = element.datum().active;
	    var activeColorMode = getColorModeByState(isActive);
	    
	    element.classed("active", isActive)
	      .text(activeColorMode.text);
	    
	    if ( colorExternalsMode ) {
	      colorExternalsMode.colorModeType(activeColorMode.type);
	    }
	  }
	  
	  function getColorModeByState( isActive ){
	    return isActive ? GRADIENT_COLOR_MODE : SAME_COLOR_MODE;
	  }
	  
	  /**
	   * Resets the modes to their default.
	   */
	  modeMenu.reset = function (){
	    checkboxes.forEach(function ( checkbox ){
	      var defaultState = checkbox.datum().defaultState,
	        isChecked = checkbox.property("checked");
	      
	      if ( isChecked !== defaultState ) {
	        checkbox.property("checked", defaultState);
	        // Call onclick event handlers programmatically
	        checkbox.on("click")(checkbox.datum());
	      }
	      
	      // Reset the module that is connected with the checkbox
	      checkbox.datum().module.reset();
	    });
	    
	    // set the switch to active and simulate disabling
	    colorModeSwitch.datum().active = true;
	    colorModeSwitch.on("click")();
	  };
	  
	  /** importer functions **/
	  // setting manually the values of the filter
	  // no update of the gui settings, these are updated in updateSettings
	  modeMenu.setCheckBoxValue = function ( id, checked ){
	    for ( var i = 0; i < checkboxes.length; i++ ) {
	      var cbdId = checkboxes[i].attr("id");
	      
	      if ( cbdId === id ) {
	        checkboxes[i].property("checked", checked);
	        break;
	      }
	    }
	  };
	  modeMenu.getCheckBoxValue = function ( id ){
	    for ( var i = 0; i < checkboxes.length; i++ ) {
	      var cbdId = checkboxes[i].attr("id");
	      if ( cbdId === id ) {
	        return checkboxes[i].property("checked");
	      }
	    }
	  };
	  
	  modeMenu.setColorSwitchState = function ( state ){
	    // need the !state because we simulate later a click
	    modeMenu.colorModeState(!state);
	  };
	  modeMenu.setColorSwitchStateUsingURL = function ( state ){
	    // need the !state because we simulate later a click
	    modeMenu.colorModeState(!state);
	    colorModeSwitch.on("click")(true);
	  };
	  
	  
	  modeMenu.updateSettingsUsingURL = function (){
	    var silent = true;
	    checkboxes.forEach(function ( checkbox ){
	      checkbox.on("click")(checkbox.datum(), silent);
	    });
	  };
	  
	  modeMenu.updateSettings = function (){
	    var silent = true;
	    checkboxes.forEach(function ( checkbox ){
	      checkbox.on("click")(checkbox.datum(), silent);
	    });
	    // this simulates onclick and inverts its state
	    colorModeSwitch.on("click")(silent);
	  };
	  return modeMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 330:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {module.exports = function ( graph ){
	  var debugMenu = {},
	    checkboxes = [];
	  
	  
	  var hoverFlag = false;
	  var specialCbx;
	  debugMenu.setup = function (){
	    var menuEntry = d3.select("#debugMenuHref");
	    
	    menuEntry.on("mouseover", function (){
	      if ( hoverFlag === false ) {
	        var searchMenu = graph.options().searchMenu();
	        searchMenu.hideSearchEntries();
	        specialCbx.on("click")(true);
	        if ( graph.editorMode() === false ) {
	          d3.select("#useAccuracyHelper").style("color", "#979797");
	          d3.select("#useAccuracyHelper").style("pointer-events", "none");
	          
	          // regardless the state on which useAccuracyHelper is , we are not in editing mode -> disable it
	          d3.select("#showDraggerObject").style("color", "#979797");
	          d3.select("#showDraggerObject").style("pointer-events", "none");
	        } else {
	          d3.select("#useAccuracyHelper").style("color", "#2980b9");
	          d3.select("#useAccuracyHelper").style("pointer-events", "auto");
	        }
	        hoverFlag = true;
	      }
	    });
	    menuEntry.on("mouseout", function (){
	      hoverFlag = false;
	    });
	    
	    
	    specialCbx = addCheckBox("useAccuracyHelper", "Use accuracy helper", "#useAccuracyHelper", graph.options().useAccuracyHelper,
	      function ( enabled, silent ){
	        if ( !enabled ) {
	          d3.select("#showDraggerObject").style("color", "#979797");
	          d3.select("#showDraggerObject").style("pointer-events", "none");
	          d3.select("#showDraggerObjectConfigCheckbox").node().checked = false;
	        } else {
	          d3.select("#showDraggerObject").style("color", "#2980b9");
	          d3.select("#showDraggerObject").style("pointer-events", "auto");
	        }
	        
	        if ( silent === true ) return;
	        graph.lazyRefresh();
	        graph.updateDraggerElements();
	      }
	    );
	    addCheckBox("showDraggerObject", "Show accuracy helper", "#showDraggerObject", graph.options().showDraggerObject,
	      function ( enabled, silent ){
	        if ( silent === true ) return;
	        graph.lazyRefresh();
	        graph.updateDraggerElements();
	      });
	    addCheckBox("showFPS_Statistics", "Show rendering statistics", "#showFPS_Statistics", graph.options().showRenderingStatistic,
	      function ( enabled, silent ){
	        
	        if ( graph.options().getHideDebugFeatures() === false ) {
	          d3.select("#FPS_Statistics").classed("hidden", !enabled);
	        } else {
	          d3.select("#FPS_Statistics").classed("hidden", true);
	        }
	        
	        
	      });
	    addCheckBox("showModeOfOperation", "Show input modality", "#showModeOfOperation", graph.options().showInputModality,
	      function ( enabled ){
	        if ( graph.options().getHideDebugFeatures() === false ) {
	          d3.select("#modeOfOperationString").classed("hidden", !enabled);
	        } else {
	          d3.select("#modeOfOperationString").classed("hidden", true);
	        }
	      });
	    
	    
	  };
	  
	  
	  function addCheckBox( identifier, modeName, selector, onChangeFunc, _callbackFunction ){
	    var configOptionContainer = d3.select(selector)
	      .append("div")
	      .classed("checkboxContainer", true);
	    var configCheckbox = configOptionContainer.append("input")
	      .classed("moduleCheckbox", true)
	      .attr("id", identifier + "ConfigCheckbox")
	      .attr("type", "checkbox")
	      .property("checked", onChangeFunc());
	    
	    
	    configCheckbox.on("click", function ( silent ){
	      var isEnabled = configCheckbox.property("checked");
	      onChangeFunc(isEnabled);
	      _callbackFunction(isEnabled, silent);
	      
	    });
	    checkboxes.push(configCheckbox);
	    configOptionContainer.append("label")
	      .attr("for", identifier + "ConfigCheckbox")
	      .text(modeName);
	    
	    return configCheckbox;
	  }
	  
	  debugMenu.setCheckBoxValue = function ( identifier, value ){
	    for ( var i = 0; i < checkboxes.length; i++ ) {
	      var cbdId = checkboxes[i].attr("id");
	      if ( cbdId === identifier ) {
	        checkboxes[i].property("checked", value);
	        break;
	      }
	    }
	  };
	  
	  debugMenu.getCheckBoxValue = function ( id ){
	    for ( var i = 0; i < checkboxes.length; i++ ) {
	      var cbdId = checkboxes[i].attr("id");
	      if ( cbdId === id ) {
	        return checkboxes[i].property("checked");
	      }
	    }
	  };
	  
	  debugMenu.updateSettings = function (){
	    d3.selectAll(".debugOption").classed("hidden", graph.options().getHideDebugFeatures());
	    
	    var silent = true;
	    checkboxes.forEach(function ( checkbox ){
	      checkbox.on("click")(silent);
	    });
	    if ( graph.editorMode() === false ) {
	      
	      d3.select("#useAccuracyHelper").style("color", "#979797");
	      d3.select("#useAccuracyHelper").style("pointer-events", "none");
	      
	      // regardless the state on which useAccuracyHelper is , we are not in editing mode -> disable it
	      d3.select("#showDraggerObject").style("color", "#979797");
	      d3.select("#showDraggerObject").style("pointer-events", "none");
	    } else {
	      
	      d3.select("#useAccuracyHelper").style("color", "#2980b9");
	      d3.select("#useAccuracyHelper").style("pointer-events", "auto");
	    }
	    
	  };
	  
	  return debugMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 331:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {var unescape = __webpack_require__(332);
	
	module.exports = function ( graph ){
	  
	  var ontologyMenu = {},
	    loadingInfo = d3.select("#loading-info"),
	    loadingProgress = d3.select("#loading-progress"),
	    
	    ontologyMenuTimeout,
	    fileToLoad,
	    stopTimer = false,
	    loadingError = false,
	    loadingStatusTimer,
	    conversion_sessionId,
	    cachedConversions = {},
	    loadingModule,
	    loadOntologyFromText;
	  var currentLoadedOntologyName = "";
	  
	  String.prototype.beginsWith = function ( string ){
	    return (this.indexOf(string) === 0);
	  };
	  
	  ontologyMenu.getLoadingFunction = function (){
	    return loadOntologyFromText;
	  };
	  
	  ontologyMenu.clearCachedVersion = function (){
	    if ( cachedConversions[currentLoadedOntologyName] ) {
	      cachedConversions[currentLoadedOntologyName] = undefined;
	    }
	  };
	  
	  
	  ontologyMenu.reloadCachedOntology = function (){
	    ontologyMenu.clearCachedVersion();
	    graph.clearGraphData();
	    loadingModule.parseUrlAndLoadOntology(false);
	  };
	  
	  ontologyMenu.cachedOntology = function ( ontoName ){
	    currentLoadedOntologyName = ontoName;
	    if ( cachedConversions[ontoName] ) {
	      var locStr = String(location.hash);
	      d3.select("#reloadSvgIcon").node().disabled = false;
	      graph.showReloadButtonAfterLayoutOptimization(true);
	      if ( locStr.indexOf("#file") > -1 ) {
	        d3.select("#reloadSvgIcon").node().disabled = true;
	        d3.select("#reloadCachedOntology").node().title = "reloading original version not possible, please reload the file";
	        d3.select("#reloadSvgIcon").classed("disabledReloadElement", true);
	        d3.select("#svgStringText").style("fill", "gray");
	        d3.select("#svgStringText").classed("noselect", true);
	      }
	      else {
	        d3.select("#reloadCachedOntology").node().title = "generate new visualization and overwrite cached ontology";
	        d3.select("#reloadSvgIcon").classed("disabledReloadElement", false);
	        d3.select("#svgStringText").style("fill", "black");
	        d3.select("#svgStringText").classed("noselect", true);
	      }
	    } else {
	      graph.showReloadButtonAfterLayoutOptimization(false);
	      
	    }
	    return cachedConversions[ontoName];
	  };
	  ontologyMenu.setCachedOntology = function ( ontoName, ontoContent ){
	    cachedConversions[ontoName] = ontoContent;
	    currentLoadedOntologyName = ontoName;
	  };
	  
	  ontologyMenu.getErrorStatus = function (){
	    return loadingError;
	  };
	  
	  ontologyMenu.setup = function ( _loadOntologyFromText ){
	    loadOntologyFromText = _loadOntologyFromText;
	    loadingModule = graph.options().loadingModule();
	    var menuEntry = d3.select("#m_select");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	    
	    setupConverterButtons();
	    setupUploadButton();
	    
	    var descriptionButton = d3.select("#error-description-button").datum({ open: false });
	    descriptionButton.on("click", function ( data ){
	      var errorContainer = d3.select("#error-description-container");
	      var errorDetailsButton = d3.select(this);
	      
	      // toggle the state
	      data.open = !data.open;
	      var descriptionVisible = data.open;
	      if ( descriptionVisible ) {
	        errorDetailsButton.text("Hide error details");
	      } else {
	        errorDetailsButton.text("Show error details");
	      }
	      errorContainer.classed("hidden", !descriptionVisible);
	    });
	    
	    setupUriListener();
	    loadingModule.setOntologyMenu(ontologyMenu);
	  };
	  
	  
	  function setupUriListener(){
	    // reload ontology when hash parameter gets changed manually
	    d3.select(window).on("hashchange", function (){
	      var oldURL = d3.event.oldURL, newURL = d3.event.newURL;
	      if ( oldURL !== newURL ) {
	        // don't reload when just the hash parameter gets appended
	        if ( newURL === oldURL + "#" ) {
	          return;
	        }
	        updateNavigationHrefs();
	        loadingModule.parseUrlAndLoadOntology();
	      }
	    });
	    updateNavigationHrefs();
	  }
	  
	  ontologyMenu.stopLoadingTimer = function (){
	    stopTimer = true;
	    clearTimeout(loadingStatusTimer);
	  };
	  
	  /**
	   * Quick fix: update all anchor tags that are used as buttons because a click on them
	   * changes the url and this will load an other ontology.
	   */
	  function updateNavigationHrefs(){
	    d3.selectAll("#menuElementContainer > li > a").attr("href", location.hash || "#");
	  }
	  
	  ontologyMenu.setIriText = function ( text ){
	    d3.select("#iri-converter-input").node().value = text;
	    d3.select("#iri-converter-button").attr("disabled", false);
	    d3.select("#iri-converter-form").on("submit")();
	  };
	  
	  ontologyMenu.clearDetailInformation = function (){
	    var bpContainer = d3.select("#bulletPoint_container");
	    var htmlCollection = bpContainer.node().children;
	    var numEntries = htmlCollection.length;
	    
	    for ( var i = 0; i < numEntries; i++ ) {
	      htmlCollection[0].remove();
	    }
	  };
	  ontologyMenu.append_message = function ( msg ){
	    // forward call
	    append_message(msg);
	  };
	  function append_message( msg ){
	    var bpContainer = d3.select("#bulletPoint_container");
	    var div = bpContainer.append("div");
	    div.node().innerHTML = msg;
	    loadingModule.scrollDownDetails();
	  }
	  
	  ontologyMenu.append_message_toLastBulletPoint = function ( msg ){
	    // forward call
	    append_message_toLastBulletPoint(msg);
	  };
	  
	  ontologyMenu.append_bulletPoint = function ( msg ){
	    // forward call
	    append_bulletPoint(msg);
	  };
	  function append_message_toLastBulletPoint( msg ){
	    var bpContainer = d3.select("#bulletPoint_container");
	    var htmlCollection = bpContainer.node().getElementsByTagName("LI");
	    var lastItem = htmlCollection.length - 1;
	    if ( lastItem >= 0 ) {
	      var oldText = htmlCollection[lastItem].innerHTML;
	      htmlCollection[lastItem].innerHTML = oldText + msg;
	    }
	    loadingModule.scrollDownDetails();
	  }
	  
	  function append_bulletPoint( msg ){
	    var bp_container = d3.select("#bulletPoint_container");
	    var bp = bp_container.append("li");
	    bp.node().innerHTML = msg;
	    d3.select("#currentLoadingStep").node().innerHTML = msg;
	    loadingModule.scrollDownDetails();
	  }
	  
	  
	  function setupConverterButtons(){
	    var iriConverterButton = d3.select("#iri-converter-button");
	    var iriConverterInput = d3.select("#iri-converter-input");
	    
	    iriConverterInput.on("input", function (){
	      keepOntologySelectionOpenShortly();
	      
	      var inputIsEmpty = iriConverterInput.property("value") === "";
	      iriConverterButton.attr("disabled", inputIsEmpty || undefined);
	    }).on("click", function (){
	      keepOntologySelectionOpenShortly();
	    });
	    
	    d3.select("#iri-converter-form").on("submit", function (){
	      var inputName = iriConverterInput.property("value");
	      
	      // remove first spaces
	      var clearedName = inputName.replace(/%20/g, " ");
	      while ( clearedName.beginsWith(" ") ) {
	        clearedName = clearedName.substr(1, clearedName.length);
	      }
	      // remove ending spaces
	      while ( clearedName.endsWith(" ") ) {
	        clearedName = clearedName.substr(0, clearedName.length - 1);
	      }
	      // check if iri is actually an url for a json file (ends with .json)
	      // create lowercase filenames;
	      inputName = clearedName;
	      var lc_iri = inputName.toLowerCase();
	      if ( lc_iri.endsWith(".json") ) {
	        location.hash = "url=" + inputName;
	        iriConverterInput.property("value", "");
	        iriConverterInput.on("input")();
	      } else {
	        location.hash = "iri=" + inputName;
	        iriConverterInput.property("value", "");
	        iriConverterInput.on("input")();
	      }
	      d3.event.preventDefault();
	      return false;
	    });
	  }
	  
	  function setupUploadButton(){
	    var input = d3.select("#file-converter-input"),
	      inputLabel = d3.select("#file-converter-label"),
	      uploadButton = d3.select("#file-converter-button");
	    
	    input.on("change", function (){
	      var selectedFiles = input.property("files");
	      if ( selectedFiles.length <= 0 ) {
	        inputLabel.text("Select ontology file");
	        uploadButton.property("disabled", true);
	      } else {
	        inputLabel.text(selectedFiles[0].name);
	        fileToLoad = selectedFiles[0].name;
	        uploadButton.property("disabled", false);
	        uploadButton.node().click();
	        // close menu;
	        graph.options().navigationMenu().hideAllMenus();
	      }
	    });
	    
	    uploadButton.on("click", function (){
	      var selectedFile = input.property("files")[0];
	      if ( !selectedFile ) {
	        return false;
	      }
	      var newHashParameter = "file=" + selectedFile.name;
	      // Trigger the reupload manually, because the iri is not changing
	      if ( location.hash === "#" + newHashParameter ) {
	        loadingModule.parseUrlAndLoadOntology();
	      } else {
	        location.hash = newHashParameter;
	      }
	    });
	  }
	  
	  function setLoadingStatusInfo( message ){
	    // check if there is a owl2vowl li item;
	    var o2vConverterContainer = d3.select("#o2vConverterContainer");
	    if ( !o2vConverterContainer.node() ) {
	      var bp_container = d3.select("#bulletPoint_container");
	      var div = bp_container.append("div");
	      o2vConverterContainer = div.append("ul");
	      o2vConverterContainer.attr("id", "o2vConverterContainer");
	      o2vConverterContainer.style("margin-left", "-25px");
	    }
	    // clear o2vConverterContainer;
	    var htmlCollection = o2vConverterContainer.node().children;
	    var numEntries = htmlCollection.length;
	    for ( var i = 0; i < numEntries; i++ ) {
	      htmlCollection[0].remove();
	    }
	    // split tokens provided by o2v messages
	    var tokens = message.split("* ");
	    var liForToken;
	    for ( var t = 0; t < tokens.length; t++ ) {
	      var tokenMessage = tokens[t];
	      // create li for tokens;
	      if ( tokenMessage.length > 0 ) {
	        liForToken = o2vConverterContainer.append("li");
	        liForToken.attr("type", "disc");
	        liForToken.node().innerHTML = tokenMessage.replace(/\n/g, "<br>");
	      }
	    }
	    if ( liForToken )
	      liForToken.node().innerHTML += "<br>";
	    
	    loadingModule.scrollDownDetails();
	  }
	  
	  ontologyMenu.setLoadingStatusInfo = function ( message ){
	    // forward call
	    setLoadingStatusInfo(message);
	  };
	  
	  function getLoadingStatusOnceCallBacked( callback, parameter ){
	    d3.xhr("loadingStatus?sessionId=" + conversion_sessionId, "application/text", function ( error, request ){
	      if ( error ) {
	        console.log("ontologyMenu getLoadingStatusOnceCallBacked throws error");
	        console.log("---------Error -----------");
	        console.log(error);
	        console.log("---------Request -----------");
	        console.log(request);
	      }
	      setLoadingStatusInfo(request.responseText);
	      callback(parameter);
	    });
	  }
	  
	  function getLoadingStatusTimeLooped(){
	    d3.xhr("loadingStatus?sessionId=" + conversion_sessionId, "application/text", function ( error, request ){
	      if ( error ) {
	        console.log("ontologyMenu getLoadingStatusTimeLooped throws error");
	        console.log("---------Error -----------");
	        console.log(error);
	        console.log("---------Request -----------");
	        console.log(request);
	      }
	      if ( stopTimer === false ) {
	        setLoadingStatusInfo(request.responseText);
	        timedLoadingStatusLogger();
	      }
	    });
	  }
	  
	  function timedLoadingStatusLogger(){
	    clearTimeout(loadingStatusTimer);
	    if ( stopTimer === false ) {
	      loadingStatusTimer = setTimeout(function (){
	        getLoadingStatusTimeLooped();
	      }, 1000);
	    }
	  }
	  
	  function callbackUpdateLoadingMessage( msg ){
	    d3.xhr("loadingStatus", "application/text", function ( error, request ){
	      if ( request !== undefined ) {
	        setLoadingStatusInfo(request.responseText + "<br>" + msg);
	      } else {
	        append_message(msg);
	      }
	    });
	  }
	  
	  ontologyMenu.setConversionID = function ( id ){
	    conversion_sessionId = id;
	  };
	  
	  ontologyMenu.callbackLoad_Ontology_FromIRI = function ( parameter ){
	    var relativePath = parameter[0];
	    var ontoName = parameter[1];
	    var localThreadId = parameter[2];
	    stopTimer = false;
	    timedLoadingStatusLogger();
	    d3.xhr(relativePath, "application/json", function ( error, request ){
	      var loadingSuccessful = !error;
	      // check if error occurred or responseText is empty
	      if ( (error !== null && error.status === 500) || (request && request.responseText.length === 0) ) {
	        clearTimeout(loadingStatusTimer);
	        stopTimer = true;
	        getLoadingStatusOnceCallBacked(callbackFromIRI_URL_ERROR, [error, request, localThreadId]);
	      }
	      var jsonText;
	      if ( loadingSuccessful ) {
	        clearTimeout(loadingStatusTimer);
	        stopTimer = true;
	        jsonText = request.responseText;
	        getLoadingStatusOnceCallBacked(callbackFromIRI_Success, [jsonText, ontoName, localThreadId]);
	      }
	    });
	  };
	  
	  
	  ontologyMenu.callbackLoad_Ontology_From_DirectInput = function ( text, parameter ){
	    var input = text;
	    var sessionId = parameter[1];
	    stopTimer = false;
	    timedLoadingStatusLogger();
	    
	    var formData = new FormData();
	    formData.append("input", input);
	    formData.append("sessionId", sessionId);
	    var xhr = new XMLHttpRequest();
	    
	    xhr.open("POST", "directInput", true);
	    xhr.onload = function (){
	      clearTimeout(loadingStatusTimer);
	      stopTimer = true;
	      getLoadingStatusOnceCallBacked(callbackForConvert, [xhr, input, sessionId]);
	    };
	    timedLoadingStatusLogger();
	    xhr.send(formData);
	    
	  };
	  function callbackFromIRI_Success( parameter ){
	    var local_conversionId = parameter[2];
	    if ( local_conversionId !== conversion_sessionId ) {
	      console.log("The conversion process for file:" + parameter[1] + " has been canceled!");
	      ontologyMenu.conversionFinished(local_conversionId);
	      return;
	    }
	    loadingModule.loadFromOWL2VOWL(parameter[0], parameter[1]);
	    ontologyMenu.conversionFinished();
	    
	  }
	  
	  function callbackFromDirectInput_Success( parameter ){
	    var local_conversionId = parameter[1];
	    if ( local_conversionId !== conversion_sessionId ) {
	      console.log("The conversion process for file:" + parameter[1] + " has been canceled!");
	      ontologyMenu.conversionFinished(local_conversionId);
	      return;
	    }
	    loadingModule.loadFromOWL2VOWL(parameter[0], "DirectInputConversionID" + local_conversionId);
	    ontologyMenu.conversionFinished();
	    
	  }
	  
	  ontologyMenu.getConversionId = function (){
	    return conversion_sessionId;
	  };
	  
	  ontologyMenu.callbackLoad_JSON_FromURL = function ( parameter ){
	    var relativePath = parameter[0];
	    var ontoName = parameter[1];
	    var local_conversionId = parameter[2];
	    stopTimer = false;
	    timedLoadingStatusLogger();
	    d3.xhr(relativePath, "application/json", function ( error, request ){
	      var loadingSuccessful = !error;
	      // check if error occurred or responseText is empty
	      if ( (error !== null && error.status === 500) || (request && request.responseText.length === 0) ) {
	        clearTimeout(loadingStatusTimer);
	        stopTimer = true;
	        loadingSuccessful = false;
	        console.log(request);
	        console.log(request.responseText.length);
	        getLoadingStatusOnceCallBacked(callbackFromJSON_URL_ERROR, [error, request, local_conversionId]);
	      }
	      if ( loadingSuccessful ) {
	        clearTimeout(loadingStatusTimer);
	        stopTimer = true;
	        var jsonText = request.responseText;
	        getLoadingStatusOnceCallBacked(callbackFromJSON_Success, [jsonText, ontoName, local_conversionId]);
	      }
	    });
	  };
	  
	  function callbackFromJSON_Success( parameter ){
	    var local_conversionId = parameter[2];
	    if ( local_conversionId !== conversion_sessionId ) {
	      console.log("The conversion process for file:" + parameter[1] + " has been canceled!");
	      return;
	    }
	    loadingModule.loadFromOWL2VOWL(parameter[0], parameter[1]);
	  }
	  
	  function callbackFromJSON_URL_ERROR( parameter ){
	    var error = parameter[0];
	    var request = parameter[1];
	    var local_conversionId = parameter[2];
	    if ( local_conversionId !== conversion_sessionId ) {
	      console.log("This thread has been canceled!!");
	      ontologyMenu.conversionFinished(local_conversionId);
	      return;
	    }
	    callbackUpdateLoadingMessage("<br><span style='color:red'> Failed to convert the file.</span> " +
	      " Ontology could not be loaded.<br>Is it a valid OWL ontology? Please check with <a target=\"_blank\"" +
	      "href=\"http://visualdataweb.de/validator/\">OWL Validator</a>");
	    
	    if ( error !== null && error.status === 500 ) {
	      append_message("<span style='color:red'>Could not find ontology  at the URL</span>");
	    }
	    if ( request && request.responseText.length === 0 ) {
	      append_message("<span style='color:red'>Received empty graph</span>");
	    }
	    graph.handleOnLoadingError();
	    ontologyMenu.conversionFinished();
	  }
	  
	  
	  function callbackFromIRI_URL_ERROR( parameter ){
	    var error = parameter[0];
	    var request = parameter[1];
	    var local_conversionId = parameter[2];
	    if ( local_conversionId !== conversion_sessionId ) {
	      console.log("This thread has been canceled!!");
	      ontologyMenu.conversionFinished(local_conversionId);
	      return;
	    }
	    callbackUpdateLoadingMessage("<br><span style='color:red'> Failed to convert the file.</span> " +
	      " Ontology could not be loaded.<br>Is it a valid OWL ontology? Please check with <a target=\"_blank\"" +
	      "href=\"http://visualdataweb.de/validator/\">OWL Validator</a>");
	    
	    if ( error !== null && error.status === 500 ) {
	      append_message("<span style='color:red'>Could not find ontology  at the URL</span>");
	    }
	    if ( request && request.responseText.length === 0 ) {
	      append_message("<span style='color:red'>Received empty graph</span>");
	    }
	    graph.handleOnLoadingError();
	    ontologyMenu.conversionFinished();
	  }
	  
	  function callbackFromDirectInput_ERROR( parameter ){
	    
	    var error = parameter[0];
	    var request = parameter[1];
	    var local_conversionId = parameter[2];
	    if ( local_conversionId !== conversion_sessionId ) {
	      console.log("The loading process for direct input has been canceled!");
	      return;
	    }
	    // callbackUpdateLoadingMessage("<br> <span style='color:red'> Failed to convert the file.</span> "+
	    //     "Ontology could not be loaded.<br>Is it a valid OWL ontology? Please check with <a target=\"_blank\"" +
	    //     "href=\"http://visualdataweb.de/validator/\">OWL Validator</a>");
	    if ( error !== null && error.status === 500 ) {
	      append_message("<span style='color:red'>Could not convert direct input</span>");
	    }
	    if ( request && request.responseText.length === 0 ) {
	      append_message("<span style='color:red'>Received empty graph</span>");
	    }
	    
	    graph.handleOnLoadingError();
	    ontologyMenu.conversionFinished();
	  }
	  
	  ontologyMenu.callbackLoadFromOntology = function ( selectedFile, filename, local_threadId ){
	    callbackLoadFromOntology(selectedFile, filename, local_threadId);
	  };
	  
	  function callbackLoadFromOntology( selectedFile, filename, local_threadId ){
	    stopTimer = false;
	    timedLoadingStatusLogger();
	    
	    var formData = new FormData();
	    formData.append("ontology", selectedFile);
	    formData.append("sessionId", local_threadId);
	    var xhr = new XMLHttpRequest();
	    
	    xhr.open("POST", "convert", true);
	    xhr.onload = function (){
	      clearTimeout(loadingStatusTimer);
	      stopTimer = true;
	      console.log(xhr);
	      getLoadingStatusOnceCallBacked(callbackForConvert, [xhr, filename, local_threadId]);
	    };
	    timedLoadingStatusLogger();
	    xhr.send(formData);
	  }
	  
	  function callbackForConvert( parameter ){
	    var xhr = parameter[0];
	    var filename = parameter[1];
	    var local_threadId = parameter[2];
	    if ( local_threadId !== conversion_sessionId ) {
	      console.log("The conversion process for file:" + filename + " has been canceled!");
	      ontologyMenu.conversionFinished(local_threadId);
	      return;
	    }
	    if ( xhr.status === 200 ) {
	      loadingModule.loadFromOWL2VOWL(xhr.responseText, filename);
	      ontologyMenu.conversionFinished();
	    } else {
	      var uglyJson=xhr.responseText;
	      var jsonResut=JSON.parse(uglyJson);
	      var niceJSON=JSON.stringify(jsonResut, 'null', '  ');
	      niceJSON= niceJSON.replace(new RegExp('\r?\n','g'), '<br />');
	      callbackUpdateLoadingMessage("Failed to convert the file. " +
	          "<br />Server answer: <br />"+
	          "<hr>"+niceJSON+ "<hr>"+
	        "Ontology could not be loaded.<br />Is it a valid OWL ontology? Please check with <a target=\"_blank\"" +
	        "href=\"http://visualdataweb.de/validator/\">OWL Validator</a>");
	      
	      graph.handleOnLoadingError();
	      ontologyMenu.conversionFinished();
	    }
	  }
	  
	  ontologyMenu.conversionFinished = function ( id ){
	    var local_id = conversion_sessionId;
	    if ( id ) {
	      local_id = id;
	    }
	    d3.xhr("conversionDone?sessionId=" + local_id, "application/text", function ( error, request ){
	      if ( error ) {
	        console.log("ontologyMenu conversionFinished throws error");
	        console.log("---------Error -----------");
	        console.log(error);
	        console.log("---------Request -----------");
	        console.log(request);
	      }
	    });
	  };
	  
	  function keepOntologySelectionOpenShortly(){
	    // Events in the menu should not be considered
	    var ontologySelection = d3.select("#select .toolTipMenu");
	    ontologySelection.on("click", function (){
	      d3.event.stopPropagation();
	    }).on("keydown", function (){
	      d3.event.stopPropagation();
	    });
	    
	    ontologySelection.style("display", "block");
	    
	    function disableKeepingOpen(){
	      ontologySelection.style("display", undefined);
	      
	      clearTimeout(ontologyMenuTimeout);
	      d3.select(window).on("click", undefined).on("keydown", undefined);
	      ontologySelection.on("mouseover", undefined);
	    }
	    
	    // Clear the timeout to handle fast calls of this function
	    clearTimeout(ontologyMenuTimeout);
	    ontologyMenuTimeout = setTimeout(function (){
	      disableKeepingOpen();
	    }, 3000);
	    
	    // Disable forced open selection on interaction
	    d3.select(window).on("click", function (){
	      disableKeepingOpen();
	    }).on("keydown", function (){
	      disableKeepingOpen();
	    });
	    
	    ontologySelection.on("mouseover", function (){
	      disableKeepingOpen();
	    });
	  }
	  
	  ontologyMenu.showLoadingStatus = function ( visible ){
	    if ( visible === true ) {
	      displayLoadingIndicators();
	    }
	    else {
	      hideLoadingInformations();
	    }
	  };
	  
	  function displayLoadingIndicators(){
	    d3.select("#layoutLoadingProgressBarContainer").classed("hidden", false);
	    loadingInfo.classed("hidden", false);
	    loadingProgress.classed("hidden", false);
	  }
	  
	  function hideLoadingInformations(){
	    loadingInfo.classed("hidden", true);
	  }
	  
	  return ontologyMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 332:
/***/ (function(module, exports, __webpack_require__) {

	var toString = __webpack_require__(221),
	    unescapeHtmlChar = __webpack_require__(333);
	
	/** Used to match HTML entities and HTML characters. */
	var reEscapedHtml = /&(?:amp|lt|gt|quot|#39);/g,
	    reHasEscapedHtml = RegExp(reEscapedHtml.source);
	
	/**
	 * The inverse of `_.escape`; this method converts the HTML entities
	 * `&amp;`, `&lt;`, `&gt;`, `&quot;`, and `&#39;` in `string` to
	 * their corresponding characters.
	 *
	 * **Note:** No other HTML entities are unescaped. To unescape additional
	 * HTML entities use a third-party library like [_he_](https://mths.be/he).
	 *
	 * @static
	 * @memberOf _
	 * @since 0.6.0
	 * @category String
	 * @param {string} [string=''] The string to unescape.
	 * @returns {string} Returns the unescaped string.
	 * @example
	 *
	 * _.unescape('fred, barney, &amp; pebbles');
	 * // => 'fred, barney, & pebbles'
	 */
	function unescape(string) {
	  string = toString(string);
	  return (string && reHasEscapedHtml.test(string))
	    ? string.replace(reEscapedHtml, unescapeHtmlChar)
	    : string;
	}
	
	module.exports = unescape;


/***/ }),

/***/ 333:
/***/ (function(module, exports, __webpack_require__) {

	var basePropertyOf = __webpack_require__(334);
	
	/** Used to map HTML entities to characters. */
	var htmlUnescapes = {
	  '&amp;': '&',
	  '&lt;': '<',
	  '&gt;': '>',
	  '&quot;': '"',
	  '&#39;': "'"
	};
	
	/**
	 * Used by `_.unescape` to convert HTML entities to characters.
	 *
	 * @private
	 * @param {string} chr The matched character to unescape.
	 * @returns {string} Returns the unescaped character.
	 */
	var unescapeHtmlChar = basePropertyOf(htmlUnescapes);
	
	module.exports = unescapeHtmlChar;


/***/ }),

/***/ 334:
/***/ (function(module, exports) {

	/**
	 * The base implementation of `_.propertyOf` without support for deep paths.
	 *
	 * @private
	 * @param {Object} object The object to query.
	 * @returns {Function} Returns the new accessor function.
	 */
	function basePropertyOf(object) {
	  return function(key) {
	    return object == null ? undefined : object[key];
	  };
	}
	
	module.exports = basePropertyOf;


/***/ }),

/***/ 335:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the logic for the pause and resume button.
	 *
	 * @param graph the associated webvowl graph
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  
	  var pauseMenu = {},
	    pauseButton;
	  
	  
	  /**
	   * Adds the pause button to the website.
	   */
	  pauseMenu.setup = function (){
	    var menuEntry = d3.select("#pauseOption");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	    pauseButton = d3.select("#pause-button")
	      .datum({ paused: false })
	      .on("click", function ( d ){
	        graph.paused(!d.paused);
	        d.paused = !d.paused;
	        updatePauseButton();
	        pauseButton.classed("highlighted", d.paused);
	      });
	    // Set these properties the first time manually
	    updatePauseButton();
	  };
	  
	  pauseMenu.setPauseValue = function ( value ){
	    pauseButton.datum().paused = value;
	    graph.paused(value);
	    pauseButton.classed("highlighted", value);
	    updatePauseButton();
	  };
	  
	  function updatePauseButton(){
	    updatePauseButtonClass();
	    updatePauseButtonText();
	  }
	  
	  function updatePauseButtonClass(){
	    pauseButton.classed("paused", function ( d ){
	      return d.paused;
	    });
	  }
	  
	  function updatePauseButtonText(){
	    if ( pauseButton.datum().paused ) {
	      pauseButton.text("Resume");
	    } else {
	      pauseButton.text("Pause");
	    }
	  }
	  
	  pauseMenu.reset = function (){
	    // resuming
	    pauseMenu.setPauseValue(false);
	  };
	  
	  
	  return pauseMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 336:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the logic for the reset button.
	 *
	 * @param graph the associated webvowl graph
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  
	  var resetMenu = {},
	    options = graph.graphOptions(),
	    resettableModules,
	    untouchedOptions = webvowl.options();
	  
	  
	  /**
	   * Adds the reset button to the website.
	   * @param _resettableModules modules that can be resetted
	   */
	  resetMenu.setup = function ( _resettableModules ){
	    resettableModules = _resettableModules;
	    d3.select("#reset-button").on("click", resetGraph);
	    var menuEntry = d3.select("#resetOption");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	  };
	  
	  function resetGraph(){
	    graph.resetSearchHighlight();
	    graph.options().searchMenu().clearText();
	    options.classDistance(untouchedOptions.classDistance());
	    options.datatypeDistance(untouchedOptions.datatypeDistance());
	    options.charge(untouchedOptions.charge());
	    options.gravity(untouchedOptions.gravity());
	    options.linkStrength(untouchedOptions.linkStrength());
	    graph.reset();
	    
	    resettableModules.forEach(function ( module ){
	      module.reset();
	    });
	    
	    graph.updateStyle();
	  }
	  
	  
	  return resetMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 337:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the search "engine"
	 *
	 * @param graph the associated webvowl graph
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  var searchMenu = {},
	    dictionary = [],
	    entryNames = [],
	    searchLineEdit,
	    mergedStringsList,
	    mergedIdList,
	    maxEntries = 6,
	    dictionaryUpdateRequired = true,
	    labelDictionary,
	    inputText,
	    viewStatusOfSearchEntries = false;
	  
	  var results = [];
	  var resultID = [];
	  var c_locate = d3.select("#locateSearchResult");
	  var c_search = d3.select("#c_search");
	  var m_search = d3.select("#m_search"); // << dropdown container;
	  
	  
	  String.prototype.beginsWith = function ( string ){
	    return (this.indexOf(string) === 0);
	  };
	  
	  searchMenu.requestDictionaryUpdate = function (){
	    dictionaryUpdateRequired = true;
	    // clear possible pre searched entries
	    var htmlCollection = m_search.node().children;
	    var numEntries = htmlCollection.length;
	    
	    for ( var i = 0; i < numEntries; i++ )
	      htmlCollection[0].remove();
	    searchLineEdit.node().value = "";
	  };
	  
	  
	  function updateSearchDictionary(){
	    labelDictionary = graph.getUpdateDictionary();
	    dictionaryUpdateRequired = false;
	    dictionary = [];
	    entryNames = [];
	    var idList = [];
	    var stringList = [];
	    
	    var i;
	    for ( i = 0; i < labelDictionary.length; i++ ) {
	      var lEntry = labelDictionary[i].labelForCurrentLanguage();
	      idList.push(labelDictionary[i].id());
	      stringList.push(lEntry);
	      // add all equivalents to the search space;
	      if ( labelDictionary[i].equivalents && labelDictionary[i].equivalents().length > 0 ) {
	        var eqs = labelDictionary[i].equivalentsString();
	        var eqsLabels = eqs.split(", ");
	        for ( var e = 0; e < eqsLabels.length; e++ ) {
	          idList.push(labelDictionary[i].id());
	          stringList.push(eqsLabels[e]);
	        }
	      }
	    }
	    
	    mergedStringsList = [];
	    mergedIdList = [];
	    var indexInStringList = -1;
	    var currentString;
	    var currentObjectId;
	    
	    for ( i = 0; i < stringList.length; i++ ) {
	      if ( i === 0 ) {
	        // just add the elements
	        mergedStringsList.push(stringList[i]);
	        mergedIdList.push([]);
	        mergedIdList[0].push(idList[i]);
	        continue;
	      }
	      else {
	        currentString = stringList[i];
	        currentObjectId = idList[i];
	        indexInStringList = mergedStringsList.indexOf(currentString);
	      }
	      if ( indexInStringList === -1 ) {
	        mergedStringsList.push(stringList[i]);
	        mergedIdList.push([]);
	        var lastEntry = mergedIdList.length;
	        mergedIdList[lastEntry - 1].push(currentObjectId);
	      } else {
	        mergedIdList[indexInStringList].push(currentObjectId);
	      }
	    }
	    
	    for ( i = 0; i < mergedStringsList.length; i++ ) {
	      var aString = mergedStringsList[i];
	      var correspondingIdList = mergedIdList[i];
	      var idListResult = "[ ";
	      for ( var j = 0; j < correspondingIdList.length; j++ ) {
	        idListResult = idListResult + correspondingIdList[j].toString();
	        idListResult = idListResult + ", ";
	      }
	      idListResult = idListResult.substring(0, idListResult.length - 2);
	      idListResult = idListResult + " ]";
	      
	      dictionary.push(aString);
	      entryNames.push(aString);
	    }
	  }
	  
	  searchMenu.setup = function (){
	    // clear dictionary;
	    dictionary = [];
	    searchLineEdit = d3.select("#search-input-text");
	    searchLineEdit.on("input", userInput);
	    searchLineEdit.on("keydown", userNavigation);
	    searchLineEdit.on("click", toggleSearchEntryView);
	    searchLineEdit.on("mouseover", hoverSearchEntryView);
	    
	    c_locate.on("click", function (){
	      graph.locateSearchResult();
	    });
	    
	    c_locate.on("mouseover", function (){
	      searchMenu.hideSearchEntries();
	    });
	    
	  };
	  
	  function hoverSearchEntryView(){
	    updateSelectionStatusFlags();
	    searchMenu.showSearchEntries();
	  }
	  
	  function toggleSearchEntryView(){
	    if ( viewStatusOfSearchEntries ) {
	      searchMenu.hideSearchEntries();
	    } else {
	      searchMenu.showSearchEntries();
	    }
	  }
	  
	  searchMenu.hideSearchEntries = function (){
	    m_search.style("display", "none");
	    viewStatusOfSearchEntries = false;
	  };
	  
	  searchMenu.showSearchEntries = function (){
	    m_search.style("display", "block");
	    viewStatusOfSearchEntries = true;
	  };
	  
	  function ValidURL( str ){
	    var urlregex = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/;
	    return urlregex.test(str);
	    
	  }
	  
	  
	  function updateSelectionStatusFlags(){
	    if ( searchLineEdit.node().value.length === 0 ) {
	      createSearchEntries();
	      return;
	    }
	    handleAutoCompletion();
	  }
	  
	  function userNavigation(){
	    if ( dictionaryUpdateRequired ) {
	      updateSearchDictionary();
	    }
	    
	    var htmlCollection = m_search.node().children;
	    var numEntries = htmlCollection.length;
	    
	    
	    var move = 0;
	    var i;
	    var selectedEntry = -1;
	    for ( i = 0; i < numEntries; i++ ) {
	      var atr = htmlCollection[i].getAttribute('class');
	      if ( atr === "dbEntrySelected" ) {
	        selectedEntry = i;
	      }
	    }
	    if ( d3.event.keyCode === 13 ) {
	      if ( selectedEntry >= 0 && selectedEntry < numEntries ) {
	        // simulate onClick event
	        htmlCollection[selectedEntry].onclick();
	        searchMenu.hideSearchEntries();
	      }
	      else if ( numEntries === 0 ) {
	        inputText = searchLineEdit.node().value;
	        // check if input text ends or begins with with space
	        // remove first spaces
	        var clearedText = inputText.replace(/%20/g, " ");
	        while ( clearedText.beginsWith(" ") ) {
	          clearedText = clearedText.substr(1, clearedText.length);
	        }
	        // remove ending spaces
	        while ( clearedText.endsWith(" ") ) {
	          clearedText = clearedText.substr(0, clearedText.length - 1);
	        }
	        var iri = clearedText.replace(/ /g, "%20");
	        
	        var valid = ValidURL(iri);
	        // validate url:
	        if ( valid ) {
	          var ontM = graph.options().ontologyMenu();
	          ontM.setIriText(iri);
	          searchLineEdit.node().value = "";
	        }
	        else {
	          console.log(iri + " is not a valid URL!");
	        }
	      }
	    }
	    if ( d3.event.keyCode === 38 ) {
	      move = -1;
	      searchMenu.showSearchEntries();
	    }
	    if ( d3.event.keyCode === 40 ) {
	      move = +1;
	      searchMenu.showSearchEntries();
	    }
	    
	    var newSelection = selectedEntry + move;
	    if ( newSelection !== selectedEntry ) {
	      
	      if ( newSelection < 0 && selectedEntry <= 0 ) {
	        htmlCollection[0].setAttribute('class', "dbEntrySelected");
	      }
	      
	      if ( newSelection >= numEntries ) {
	        htmlCollection[selectedEntry].setAttribute('class', "dbEntrySelected");
	      }
	      if ( newSelection >= 0 && newSelection < numEntries ) {
	        htmlCollection[newSelection].setAttribute('class', "dbEntrySelected");
	        if ( selectedEntry >= 0 )
	          htmlCollection[selectedEntry].setAttribute('class', "dbEntry");
	      }
	    }
	  }
	  
	  searchMenu.getSearchString = function (){
	    return searchLineEdit.node().value;
	  };
	  
	  
	  function clearSearchEntries(){
	    var htmlCollection = m_search.node().children;
	    var numEntries = htmlCollection.length;
	    for ( var i = 0; i < numEntries; i++ ) {
	      htmlCollection[0].remove();
	    }
	    results = [];
	    resultID = [];
	    
	  }
	  
	  function createSearchEntries(){
	    inputText = searchLineEdit.node().value;
	    var i;
	    var lc_text = inputText.toLowerCase();
	    var token;
	    
	    for ( i = 0; i < dictionary.length; i++ ) {
	      var tokenElement = dictionary[i];
	      if ( tokenElement === undefined ) {
	        //@WORKAROUND : nodes with undefined labels are skipped
	        //@FIX: these nodes are now not added to the dictionary
	        continue;
	      }
	      token = dictionary[i].toLowerCase();
	      if ( token.indexOf(lc_text) > -1 ) {
	        results.push(dictionary[i]);
	        resultID.push(i);
	      }
	    }
	  }
	  
	  function measureTextWidth( text, textStyle ){
	    // Set a default value
	    if ( !textStyle ) {
	      textStyle = "text";
	    }
	    var d = d3.select("body")
	        .append("div")
	        .attr("class", textStyle)
	        .attr("id", "width-test") // tag this element to identify it
	        .attr("style", "position:absolute; float:left; white-space:nowrap; visibility:hidden;")
	        .text(text),
	      w = document.getElementById("width-test").offsetWidth;
	    d.remove();
	    return w;
	  }
	  
	  function cropText( input ){
	    var maxWidth = 250;
	    var textStyle = "dbEntry";
	    var truncatedText = input;
	    var textWidth;
	    var ratio;
	    var newTruncatedTextLength;
	    while ( true ) {
	      textWidth = measureTextWidth(truncatedText, textStyle);
	      if ( textWidth <= maxWidth ) {
	        break;
	      }
	      
	      ratio = textWidth / maxWidth;
	      newTruncatedTextLength = Math.floor(truncatedText.length / ratio);
	      
	      // detect if nothing changes
	      if ( truncatedText.length === newTruncatedTextLength ) {
	        break;
	      }
	      
	      truncatedText = truncatedText.substring(0, newTruncatedTextLength);
	    }
	    
	    if ( input.length > truncatedText.length ) {
	      return input.substring(0, truncatedText.length - 6);
	    }
	    return input;
	  }
	  
	  function createDropDownElements(){
	    var numEntries;
	    var copyRes = results;
	    var i;
	    var token;
	    var newResults = [];
	    var newResultsIds = [];
	    
	    var lc_text = searchLineEdit.node().value.toLowerCase();
	    // set the number of shown results to be maxEntries or less;
	    numEntries = results.length;
	    if ( numEntries > maxEntries )
	      numEntries = maxEntries;
	    
	    
	    for ( i = 0; i < numEntries; i++ ) {
	      // search for the best entry
	      var indexElement = 1000000;
	      var lengthElement = 1000000;
	      var bestElement = -1;
	      for ( var j = 0; j < copyRes.length; j++ ) {
	        token = copyRes[j].toLowerCase();
	        var tIe = token.indexOf(lc_text);
	        var tLe = token.length;
	        if ( tIe > -1 && tIe <= indexElement && tLe <= lengthElement ) {
	          bestElement = j;
	          indexElement = tIe;
	          lengthElement = tLe;
	        }
	      }
	      newResults.push(copyRes[bestElement]);
	      newResultsIds.push(resultID[bestElement]);
	      copyRes[bestElement] = "";
	    }
	    
	    // add the results to the entry menu
	    //******************************************
	    numEntries = results.length;
	    if ( numEntries > maxEntries )
	      numEntries = maxEntries;
	    
	    var filteredOutElements = 0;
	    for ( i = 0; i < numEntries; i++ ) {
	      //add results to the dropdown menu
	      var testEntry = document.createElement('li');
	      testEntry.setAttribute('elementID', newResultsIds[i]);
	      testEntry.onclick = handleClick(newResultsIds[i]);
	      testEntry.setAttribute('class', "dbEntry");
	      
	      var entries = mergedIdList[newResultsIds[i]];
	      var eLen = entries.length;
	      
	      var croppedText = cropText(newResults[i]);
	      
	      var el0 = entries[0];
	      var allSame = true;
	      var nodeMap = graph.getNodeMapForSearch();
	      var visible = eLen;
	      if ( eLen > 1 ) {
	        for ( var q = 0; q < eLen; q++ ) {
	          if ( nodeMap[entries[q]] === undefined ) {
	            visible--;
	          }
	        }
	      }
	      
	      for ( var a = 0; a < eLen; a++ ) {
	        if ( el0 !== entries[a] ) {
	          allSame = false;
	        }
	      }
	      if ( croppedText !== newResults[i] ) {
	        // append ...(#numElements) if needed
	        if ( eLen > 1 && allSame === false ) {
	          if ( eLen !== visible )
	            croppedText += "... (" + visible + "/" + eLen + ")";
	        }
	        else {
	          croppedText += "...";
	        }
	        testEntry.title = newResults[i];
	      }
	      else {
	        if ( eLen > 1 && allSame === false ) {
	          if ( eLen !== visible )
	            croppedText += " (" + visible + "/" + eLen + ")";
	          else
	            croppedText += " (" + eLen + ")";
	        }
	      }
	      
	      var searchEntryNode = d3.select(testEntry);
	      if ( eLen === 1 || allSame === true ) {
	        if ( nodeMap[entries[0]] === undefined ) {
	          searchEntryNode.style("color", "#979797");
	          testEntry.title = newResults[i] + "\nElement is filtered out.";
	          testEntry.onclick = function (){
	          };
	          d3.select(testEntry).style("cursor", "default");
	          filteredOutElements++;
	        }
	      } else {
	        if ( visible < 1 ) {
	          searchEntryNode.style("color", "#979797");
	          testEntry.onclick = function (){
	          };
	          testEntry.title = newResults[i] + "\nAll elements are filtered out.";
	          d3.select(testEntry).style("cursor", "default");
	          filteredOutElements++;
	        } else {
	          searchEntryNode.style("color", "");
	        }
	        if ( visible < eLen && visible > 1 ) {
	          testEntry.title = newResults[i] + "\n" + visible + "/" + eLen + " elements are visible.";
	        }
	      }
	      searchEntryNode.node().innerHTML = croppedText;
	      m_search.node().appendChild(testEntry);
	    }
	  }
	  
	  
	  function handleAutoCompletion(){
	    /**  pre condition: autoCompletion has already a valid text**/
	    clearSearchEntries();
	    createSearchEntries();
	    createDropDownElements();
	  }
	  
	  function userInput(){
	    c_locate.classed("highlighted", false);
	    c_locate.node().title = "Nothing to locate";
	    
	    if ( dictionaryUpdateRequired ) {
	      updateSearchDictionary();
	    }
	    graph.resetSearchHighlight();
	    
	    if ( dictionary.length === 0 ) {
	      console.log("dictionary is empty");
	      return;
	    }
	    inputText = searchLineEdit.node().value;
	    
	    clearSearchEntries();
	    if ( inputText.length !== 0 ) {
	      createSearchEntries();
	      createDropDownElements();
	    }
	    
	    searchMenu.showSearchEntries();
	  }
	  
	  function handleClick( elementId ){
	    
	    return function (){
	      var id = elementId;
	      var correspondingIds = mergedIdList[id];
	      
	      // autoComplete the text for the user
	      var autoComStr = entryNames[id];
	      searchLineEdit.node().value = autoComStr;
	      
	      graph.resetSearchHighlight();
	      graph.highLightNodes(correspondingIds);
	      c_locate.node().title = "Locate search term";
	      if ( autoComStr !== inputText ) {
	        handleAutoCompletion();
	      }
	      searchMenu.hideSearchEntries();
	    };
	  }
	  
	  searchMenu.clearText = function (){
	    searchLineEdit.node().value = "";
	    c_locate.classed("highlighted", false);
	    c_locate.node().title = "Nothing to locate";
	    var htmlCollection = m_search.node().children;
	    var numEntries = htmlCollection.length;
	    for ( var i = 0; i < numEntries; i++ ) {
	      htmlCollection[0].remove();
	    }
	  };
	  
	  return searchMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 338:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/**
	 * Contains the navigation "engine"
	 *
	 * @param graph the associated webvowl graph
	 * @returns {{}}
	 */
	module.exports = function ( graph ){
	  var navigationMenu = {},
	    scrollContainer = d3.select("#menuElementContainer").node(),
	    menuContainer = d3.select("#menuContainer").node(),
	    leftButton = d3.select("#scrollLeftButton"),
	    rightButton = d3.select("#scrollRightButton"),
	    scrolLeftValue,
	    scrollMax,
	    currentlyVisibleMenu,
	    currentlyHoveredEntry,
	    touchedElement = false,
	    t_scrollLeft,
	    t_scrollRight,
	    c_select = [],
	    m_select = [];
	  
	  
	  function clearAllTimers(){
	    cancelAnimationFrame(t_scrollLeft);
	    cancelAnimationFrame(t_scrollRight);
	  }
	  
	  function timed_scrollRight(){
	    scrolLeftValue += 5;
	    scrollContainer.scrollLeft = scrolLeftValue;
	    navigationMenu.updateScrollButtonVisibility();
	    if ( scrolLeftValue >= scrollMax ) {
	      clearAllTimers();
	      return;
	    }
	    t_scrollRight = requestAnimationFrame(timed_scrollRight);
	    
	  }
	  
	  function timed_scrollLeft(){
	    scrolLeftValue -= 5;
	    scrollContainer.scrollLeft = scrolLeftValue;
	    navigationMenu.updateScrollButtonVisibility();
	    if ( scrolLeftValue <= 0 ) {
	      clearAllTimers();
	      return;
	    }
	    t_scrollRight = requestAnimationFrame(timed_scrollLeft);
	  }
	  
	  // collect all menu entries and stuff;
	  function setupControlsAndMenus(){
	    // HEURISTIC : to match the menus and their controllers we remove the first 2 letters and match
	    c_select = [];
	    m_select = [];
	    
	    var c_temp = [];
	    var m_temp = [];
	    var i;
	    var controlElements = scrollContainer.children;
	    var numEntries = controlElements.length;
	    
	    for ( i = 0; i < numEntries; i++ ) {
	      c_temp.push(controlElements[i].id.slice(2));
	    }
	    
	    var menuElements = menuContainer.children;
	    numEntries = menuElements.length;
	    for ( i = 0; i < numEntries; i++ ) {
	      m_temp.push(menuElements[i].id.slice(2));
	    }
	    
	    numEntries = controlElements.length;
	    for ( i = 0; i < numEntries; i++ ) {
	      c_select[i] = "c_" + c_temp[i];
	      if ( m_temp.indexOf(c_temp[i]) > -1 ) {
	        m_select[i] = "m_" + c_temp[i];
	      } else {
	        m_select[i] = undefined;
	      }
	      // create custom behavior for click, touch, and hover
	      d3.select("#" + c_select[i]).on("mouseover", menuElementOnHovered);
	      d3.select("#" + c_select[i]).on("mouseout", menuElementOutHovered);
	      
	      d3.select("#" + c_select[i]).on("click", menuElementClicked);
	      d3.select("#" + c_select[i]).on("touchstart", menuElementTouched);
	      
	    }
	    
	    // connect to mouseWheel
	    d3.select("#menuElementContainer").on("wheel", function (){
	      var wheelEvent = d3.event;
	      var offset;
	      if ( wheelEvent.deltaY < 0 ) offset = 20;
	      if ( wheelEvent.deltaY > 0 ) offset = -20;
	      scrollContainer.scrollLeft += offset;
	      navigationMenu.hideAllMenus();
	      navigationMenu.updateScrollButtonVisibility();
	    });
	    
	    // connect scrollIndicator Buttons;
	    d3.select("#scrollRightButton").on("mousedown", function (){
	      scrolLeftValue = scrollContainer.scrollLeft;
	      navigationMenu.hideAllMenus();
	      t_scrollRight = requestAnimationFrame(timed_scrollRight);
	      
	    }).on("touchstart", function (){
	      scrolLeftValue = scrollContainer.scrollLeft;
	      navigationMenu.hideAllMenus();
	      t_scrollRight = requestAnimationFrame(timed_scrollRight);
	    }).on("mouseup", clearAllTimers)
	      .on("touchend", clearAllTimers)
	      .on("touchcancel", clearAllTimers);
	    
	    d3.select("#scrollLeftButton").on("mousedown", function (){
	      scrolLeftValue = scrollContainer.scrollLeft;
	      navigationMenu.hideAllMenus();
	      t_scrollLeft = requestAnimationFrame(timed_scrollLeft);
	    }).on("touchstart", function (){
	      scrolLeftValue = scrollContainer.scrollLeft;
	      navigationMenu.hideAllMenus();
	      t_scrollLeft = requestAnimationFrame(timed_scrollLeft);
	    }).on("mouseup", clearAllTimers)
	      .on("touchend", clearAllTimers)
	      .on("touchcancel", clearAllTimers);
	    
	    // connect the scroll functionality;
	    d3.select("#menuElementContainer").on("scroll", function (){
	      navigationMenu.updateScrollButtonVisibility();
	      navigationMenu.hideAllMenus();
	    });
	  }
	  
	  function menuElementOnHovered(){
	    navigationMenu.hideAllMenus();
	    if ( touchedElement ) {
	      return;
	    }
	    showSingleMenu(this.id);
	  }
	  
	  function menuElementOutHovered(){
	    hoveroutedControMenu(this.id);
	  }
	  
	  function menuElementClicked(){
	    var m_element = m_select[c_select.indexOf(this.id)];
	    if ( m_element ) {
	      var menuElement = d3.select("#" + m_element);
	      if ( menuElement ) {
	        if ( menuElement.style("display") === "block" ) {
	          menuElement.style("display", "none");// hide it
	        } else {
	          showSingleMenu(this.id);
	        }
	      }
	    }
	  }
	  
	  function menuElementTouched(){
	    // it sets a flag that we have touched it,
	    // since d3. propagates the event for touch as hover and then click, we block the hover event
	    touchedElement = true;
	  }
	  
	  
	  function hoveroutedControMenu( controllerID ){
	    currentlyHoveredEntry = d3.select("#" + controllerID);
	    if ( controllerID !== "c_search" ) {
	      d3.select("#" + controllerID).select("path").style("stroke-width", "0");
	      d3.select("#" + controllerID).select("path").style("fill", "#fff");
	    }
	    
	  }
	  
	  function showSingleMenu( controllerID ){
	    currentlyHoveredEntry = d3.select("#" + controllerID).node();
	    // get the corresponding menu element for this controller
	    var m_element = m_select[c_select.indexOf(controllerID)];
	    if ( m_element ) {
	      if ( controllerID !== "c_search" ) {
	        
	        d3.select("#" + controllerID).select("path").style("stroke-width", "0");
	        d3.select("#" + controllerID).select("path").style("fill", "#bdc3c7");
	      }
	      // show it if we have a menu
	      currentlyVisibleMenu = d3.select("#" + m_element);
	      currentlyVisibleMenu.style("display", "block");
	      if ( m_element === "m_export" )
	        graph.options().exportMenu().exportAsUrl();
	      updateMenuPosition();
	    }
	  }
	  
	  function updateMenuPosition(){
	    if ( currentlyHoveredEntry ) {
	      var leftOffset = currentlyHoveredEntry.offsetLeft;
	      var scrollOffset = scrollContainer.scrollLeft;
	      var totalOffset = leftOffset - scrollOffset;
	      var finalOffset = Math.max(0, totalOffset);
	      var fullContainer_width = scrollContainer.getBoundingClientRect().width;
	      var elementWidth = currentlyVisibleMenu.node().getBoundingClientRect().width;
	      // make priority > first check if we are right
	      if ( finalOffset + elementWidth > fullContainer_width ) {
	        finalOffset = fullContainer_width - elementWidth;
	      }
	      // fix priority;
	      finalOffset = Math.max(0, finalOffset);
	      currentlyVisibleMenu.style("left", finalOffset + "px");
	      
	      // // check if outside the viewport
	      // var menuWidth=currentlyHoveredEntry.getBoundingClientRect().width;
	      // var bt_width=36;
	      // if (totalOffset+menuWidth<bt_width || totalOffset+bt_width>fullContainer_width){
	      //     navigationMenu.hideAllMenus();
	      //     currentlyHoveredEntry=undefined;
	      // }
	    }
	  }
	  
	  navigationMenu.hideAllMenus = function (){
	    d3.selectAll(".toolTipMenu").style("display", "none"); // hiding all menus
	  };
	  
	  navigationMenu.updateScrollButtonVisibility = function (){
	    scrollMax = scrollContainer.scrollWidth - scrollContainer.clientWidth - 2;
	    if ( scrollContainer.scrollLeft === 0 ) {
	      leftButton.classed("hidden", true);
	    } else {
	      leftButton.classed("hidden", false);
	    }
	    
	    if ( scrollContainer.scrollLeft > scrollMax ) {
	      rightButton.classed("hidden", true);
	    } else {
	      rightButton.classed("hidden", false);
	    }
	    
	  };
	  
	  navigationMenu.setup = function (){
	    setupControlsAndMenus();
	    // make sure that the menu elements follow their controller and also their restrictions
	    // some hovering behavior -- lets the menu disappear when hovered in graph or sidebar;
	    d3.select("#graph").on("mouseover", function (){
	      navigationMenu.hideAllMenus();
	    });
	    d3.select("#generalDetails").on("mouseover", function (){
	      navigationMenu.hideAllMenus();
	    });
	  };
	  
	  return navigationMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 339:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {/** The zoom Slider **/
	module.exports = function ( graph ){
	  var zoomSlider = {};
	  var minMag = graph.options().minMagnification(),
	    maxMag = graph.options().maxMagnification(),
	    defZoom,
	    t_zoomOut,
	    t_zoomIn,
	    zoomValue,
	    showSlider = true,
	    w = graph.options().width(),
	    h = graph.options().height(),
	    slider;
	  
	  defZoom = Math.min(w, h) / 1000;
	  
	  function clearAllTimers(){
	    cancelAnimationFrame(t_zoomOut);
	    cancelAnimationFrame(t_zoomIn);
	  }
	  
	  function timed_zoomOut(){
	    zoomValue = 0.98 * zoomValue;
	    // fail saves
	    if ( zoomValue < minMag ) {
	      zoomValue = minMag;
	    }
	    graph.setSliderZoom(zoomValue);
	    t_zoomOut = requestAnimationFrame(timed_zoomOut);
	  }
	  
	  function timed_zoomIn(){
	    zoomValue = 1.02 * zoomValue;
	    // fail saves
	    if ( zoomValue > maxMag ) {
	      zoomValue = maxMag;
	    }
	    graph.setSliderZoom(zoomValue);
	    t_zoomIn = requestAnimationFrame(timed_zoomIn);
	  }
	  
	  zoomSlider.setup = function (){
	    slider = d3.select("#zoomSliderParagraph").append("input")
	      .datum({})
	      .attr("id", "zoomSliderElement")
	      .attr("type", "range")
	      .attr("value", defZoom)
	      .attr("min", minMag)
	      .attr("max", maxMag)
	      .attr("step", (maxMag - minMag) / 40)
	      .attr("title", "zoom factor")
	      .on("input", function (){
	        zoomSlider.zooming();
	      });
	    
	    d3.select("#zoomOutButton").on("mousedown", function (){
	      graph.options().navigationMenu().hideAllMenus();
	      zoomValue = graph.scaleFactor();
	      t_zoomOut = requestAnimationFrame(timed_zoomOut);
	    })
	      .on("touchstart", function (){
	        graph.options().navigationMenu().hideAllMenus();
	        zoomValue = graph.scaleFactor();
	        t_zoomOut = requestAnimationFrame(timed_zoomOut);
	      })
	      .on("mouseup", clearAllTimers)
	      .on("touchend", clearAllTimers)
	      .on("touchcancel", clearAllTimers)
	      .attr("title", "zoom out");
	    
	    d3.select("#zoomInButton").on("mousedown", function (){
	      graph.options().navigationMenu().hideAllMenus();
	      zoomValue = graph.scaleFactor();
	      t_zoomIn = requestAnimationFrame(timed_zoomIn);
	    })
	      .on("touchstart", function (){
	        graph.options().navigationMenu().hideAllMenus();
	        zoomValue = graph.scaleFactor();
	        t_zoomIn = requestAnimationFrame(timed_zoomIn);
	      })
	      .on("mouseup", clearAllTimers)
	      .on("touchend", clearAllTimers)
	      .on("touchcancel", clearAllTimers)
	      .attr("title", "zoom in");
	    
	    d3.select("#centerGraphButton").on("click", function (){
	      graph.options().navigationMenu().hideAllMenus();
	      graph.forceRelocationEvent();
	    }).attr("title", "center graph");
	    
	  };
	  
	  zoomSlider.showSlider = function ( val ){
	    if ( !arguments.length ) return showSlider;
	    d3.select("#zoomSlider").classed("hidden", !val);
	    showSlider = val;
	  };
	  
	  zoomSlider.zooming = function (){
	    graph.options().navigationMenu().hideAllMenus();
	    var zoomValue = slider.property("value");
	    slider.attr("value", zoomValue);
	    graph.setSliderZoom(zoomValue);
	  };
	  
	  zoomSlider.updateZoomSliderValue = function ( val ){
	    if ( slider ) {
	      slider.attr("value", val);
	      slider.property("value", val);
	    }
	  };
	  
	  return zoomSlider;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 340:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {module.exports = function ( graph ){
	  var configMenu = {},
	    checkboxes = [];
	  
	  
	  configMenu.setup = function (){
	    var menuEntry = d3.select("#m_modes");
	    menuEntry.on("mouseover", function (){
	      var searchMenu = graph.options().searchMenu();
	      searchMenu.hideSearchEntries();
	    });
	    
	    addCheckBox("showZoomSlider", "Zoom controls", "#zoomSliderOption", graph.options().zoomSlider().showSlider, 0);
	    addLabelWidthSlider("#maxLabelWidthSliderOption", "maxLabelWidth", "Max label width", graph.options().maxLabelWidth);
	  };
	  
	  
	  function addLabelWidthSlider( selector, identifier, label, onChangeFunction ){
	    var sliderContainer,
	      sliderValueLabel;
	    
	    sliderContainer = d3.select(selector)
	      .append("div")
	      .classed("distanceSliderContainer", true);
	    
	    var slider = sliderContainer.append("input")
	      .attr("id", identifier + "Slider")
	      .attr("type", "range")
	      .attr("min", 20)
	      .attr("max", 600)
	      .attr("value", onChangeFunction())
	      .attr("step", 10);
	    sliderContainer.append("label")
	      .classed("description", true)
	      .attr("for", identifier + "Slider")
	      .attr("id", identifier + "DescriptionLabel")
	      .text(label);
	    sliderValueLabel = sliderContainer.append("label")
	      .classed("value", true)
	      .attr("for", identifier + "Slider")
	      .attr("id", identifier + "valueLabel")
	      .text(onChangeFunction());
	    
	    slider.on("input", function (){
	      var value = slider.property("value");
	      onChangeFunction(value);
	      sliderValueLabel.text(value);
	      if ( graph.options().dynamicLabelWidth() === true )
	        graph.animateDynamicLabelWidth();
	    });
	    
	    // add wheel event to the slider
	    slider.on("wheel", function (){
	      if ( slider.node().disabled === true ) return;
	      var wheelEvent = d3.event;
	      var offset;
	      if ( wheelEvent.deltaY < 0 ) offset = 10;
	      if ( wheelEvent.deltaY > 0 ) offset = -10;
	      var oldVal = parseInt(slider.property("value"));
	      var newSliderValue = oldVal + offset;
	      if ( newSliderValue !== oldVal ) {
	        slider.property("value", newSliderValue);
	        onChangeFunction(newSliderValue);
	        slider.on("input")(); // << set text and update the graphStyles
	      }
	      d3.event.preventDefault();
	    });
	  }
	  
	  function addCheckBox( identifier, modeName, selector, onChangeFunc, updateLvl ){
	    var configOptionContainer = d3.select(selector)
	      .append("div")
	      .classed("checkboxContainer", true);
	    var configCheckbox = configOptionContainer.append("input")
	      .classed("moduleCheckbox", true)
	      .attr("id", identifier + "ConfigCheckbox")
	      .attr("type", "checkbox")
	      .property("checked", onChangeFunc());
	    
	    
	    configCheckbox.on("click", function ( silent ){
	      var isEnabled = configCheckbox.property("checked");
	      onChangeFunc(isEnabled);
	      if ( silent !== true ) {
	        // updating graph when silent is false or the parameter is not given.
	        if ( updateLvl === 1 ) {
	          graph.lazyRefresh();
	          //graph.redrawWithoutForce
	        }
	        if ( updateLvl === 2 ) {
	          graph.update();
	        }
	        
	        if ( updateLvl === 3 ) {
	          graph.updateDraggerElements();
	        }
	      }
	      
	    });
	    checkboxes.push(configCheckbox);
	    configOptionContainer.append("label")
	      .attr("for", identifier + "ConfigCheckbox")
	      .text(modeName);
	  }
	  
	  configMenu.setCheckBoxValue = function ( identifier, value ){
	    for ( var i = 0; i < checkboxes.length; i++ ) {
	      var cbdId = checkboxes[i].attr("id");
	      if ( cbdId === identifier ) {
	        checkboxes[i].property("checked", value);
	        break;
	      }
	    }
	  };
	  
	  configMenu.getCheckBoxValue = function ( id ){
	    for ( var i = 0; i < checkboxes.length; i++ ) {
	      var cbdId = checkboxes[i].attr("id");
	      if ( cbdId === id ) {
	        return checkboxes[i].property("checked");
	      }
	    }
	  };
	  
	  configMenu.updateSettings = function (){
	    var silent = true;
	    checkboxes.forEach(function ( checkbox ){
	      checkbox.on("click")(silent);
	    });
	  };
	  
	  return configMenu;
	};
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 341:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {module.exports = function ( graph ){
	  /** some constants **/
	  var PREDEFINED = 0,
	    FILE_UPLOAD = 1,
	    JSON_URL = 2,
	    IRI_URL = 3;
	  
	  var PROGRESS_BAR_ERROR = 0,
	    PROGRESS_BAR_BUSY = 1,
	    PROGRESS_BAR_PERCENT = 2,
	    progressBarMode = 1;
	  
	  var loadingWasSuccessFul = false;
	  var missingImportsWarning = false;
	  var showLoadingDetails = false;
	  var visibilityStatus = true;
	  
	  var DEFAULT_JSON_NAME = "foaf"; // This file is loaded by default
	  var conversion_sessionId;
	  
	  /** variable defs **/
	  var loadingModule = {},
	    menuContainer = d3.select("#loading-info"),
	    loadingInfoContainer = d3.select("#loadingInfo-container"),
	    detailsButton = d3.select("#show-loadingInfo-button"),
	    closeButton = d3.select("#loadingIndicator_closeButton"),
	    ontologyMenu,
	    ontologyIdentifierFromURL;
	  
	  /** functon defs **/
	  loadingModule.checkForScreenSize = function (){
	    // checks for window size and adjusts the loading indicator
	    var w = graph.options().width(),
	      h = graph.options().height();
	    
	    if ( w < 270 ) {
	      d3.select("#loading-info").classed("hidden", true);
	    } else {
	      // check if it should be visible
	      if ( visibilityStatus === true ) {
	        d3.select("#loading-info").classed("hidden", false);
	      } else {
	        d3.select("#loading-info").classed("hidden", true);
	      }
	    }
	    if ( h < 150 ) {
	      d3.select("#loadingInfo_msgBox").classed("hidden", true);
	    } else {
	      d3.select("#loadingInfo_msgBox").classed("hidden", false);
	    }
	    if ( h < 80 ) {
	      d3.select("#progressBarContext").classed("hidden", true);
	      d3.select("#layoutLoadingProgressBarContainer").style("height", "20px");
	    } else {
	      d3.select("#progressBarContext").classed("hidden", false);
	      d3.select("#layoutLoadingProgressBarContainer").style("height", "50px");
	    }
	  };
	  
	  loadingModule.getMessageVisibilityStatus = function (){
	    return visibilityStatus;
	  };
	  
	  loadingModule.getProgressBarMode = function (){
	    return progressBarMode;
	  };
	  
	  loadingModule.successfullyLoadedOntology = function (){
	    return loadingWasSuccessFul;
	  };
	  
	  loadingModule.missingImportsWarning = function (){
	    return missingImportsWarning;
	  };
	  
	  loadingModule.setOntologyMenu = function ( m ){
	    ontologyMenu = m;
	  };
	  
	  loadingModule.showErrorDetailsMessage = function (){
	    loadingModule.showLoadingIndicator();
	    loadingModule.expandDetails();
	    d3.select("#loadingIndicator_closeButton").classed("hidden", true);
	    loadingModule.scrollDownDetails();
	  };
	  
	  loadingModule.showWarningDetailsMessage = function (){
	    d3.select("#currentLoadingStep").style("color", "#ff0");
	    loadingModule.showLoadingIndicator();
	    loadingModule.expandDetails();
	    d3.select("#loadingIndicator_closeButton").classed("hidden", false);
	    loadingModule.scrollDownDetails();
	  };
	  
	  loadingModule.scrollDownDetails = function (){
	    var scrollingElement = d3.select("#loadingInfo-container").node();
	    scrollingElement.scrollTop = scrollingElement.scrollHeight;
	  };
	  
	  loadingModule.hideLoadingIndicator = function (){
	    d3.select("#loading-info").classed("hidden", true);
	    visibilityStatus = false;
	  };
	  
	  loadingModule.showLoadingIndicator = function (){
	    d3.select("#loading-info").classed("hidden", false);
	    visibilityStatus = true;
	    
	  };
	  
	  /** -- SETUP -- **/
	  loadingModule.setup = function (){
	    // create connections for close and details button;
	    loadingInfoContainer.classed("hidden", !showLoadingDetails);
	    detailsButton.on("click", function (){
	      showLoadingDetails = !showLoadingDetails;
	      loadingInfoContainer.classed("hidden", !showLoadingDetails);
	      detailsButton.classed("accordion-trigger-active", showLoadingDetails);
	    });
	    
	    closeButton.on("click", function (){
	      menuContainer.classed("hidden", true);
	    });
	    loadingModule.setBusyMode();
	  };
	  
	  loadingModule.updateSize = function (){
	    showLoadingDetails = !(loadingInfoContainer.classed("hidden"));
	    loadingInfoContainer.classed("hidden", !showLoadingDetails);
	    detailsButton.classed("accordion-trigger-active", showLoadingDetails);
	  };
	  
	  loadingModule.getDetailsState = function (){
	    return showLoadingDetails;
	  };
	  
	  loadingModule.expandDetails = function (){
	    showLoadingDetails = true;
	    loadingInfoContainer.classed("hidden", !showLoadingDetails);
	    detailsButton.classed("accordion-trigger-active", showLoadingDetails);
	  };
	  
	  loadingModule.collapseDetails = function (){
	    showLoadingDetails = false;
	    loadingInfoContainer.classed("hidden", !showLoadingDetails);
	    detailsButton.classed("accordion-trigger-active", showLoadingDetails);
	  };
	  
	  loadingModule.setBusyMode = function (){
	    d3.select("#currentLoadingStep").style("color", "#fff");
	    d3.select("#progressBarValue").node().innherHTML = "";
	    d3.select("#progressBarValue").style("width", "20%");
	    d3.select("#progressBarValue").classed("busyProgressBar", true);
	    progressBarMode = PROGRESS_BAR_BUSY;
	  };
	  
	  loadingModule.setSuccessful = function (){
	    d3.select("#currentLoadingStep").style("color", "#0f0");
	  };
	  
	  loadingModule.setErrorMode = function (){
	    d3.select("#currentLoadingStep").style("color", "#f00");
	    d3.select("#progressBarValue").style("width", "0%");
	    d3.select("#progressBarValue").classed("busyProgressBar", false);
	    d3.select("#progressBarValue").node().innherHTML = "";
	    progressBarMode = PROGRESS_BAR_ERROR;
	  };
	  
	  loadingModule.setPercentMode = function (){
	    d3.select("#currentLoadingStep").style("color", "#fff");
	    d3.select("#progressBarValue").classed("busyProgressBar", false);
	    d3.select("#progressBarValue").node().innherHTML = "0%";
	    d3.select("#progressBarValue").style("width", "0%");
	    progressBarMode = PROGRESS_BAR_PERCENT;
	  };
	  
	  loadingModule.setPercentValue = function ( val ){
	    d3.select("#progressBarValue").node().innherHTML = val;
	  };
	  
	  loadingModule.emptyGraphContentError = function (){
	    graph.clearGraphData();
	    ontologyMenu.append_message_toLastBulletPoint("<span style='color:red;'>failed</span>");
	    ontologyMenu.append_message_toLastBulletPoint("<br><span style=\"color:red;\">Error: Received empty graph</span>");
	    loadingWasSuccessFul = false;
	    graph.handleOnLoadingError();
	    loadingModule.setErrorMode();
	  };
	  
	  loadingModule.isThreadCanceled = function (){
	    
	  };
	  
	  loadingModule.initializeLoader = function ( storeCache ){
	    if ( storeCache === true && graph.getCachedJsonObj() !== null ) {
	      // save cached ontology;
	      var cachedContent = JSON.stringify(graph.getCachedJsonObj());
	      var cachedName = ontologyIdentifierFromURL;
	      ontologyMenu.setCachedOntology(cachedName, cachedContent);
	    }
	    conversion_sessionId = -10000;
	    ontologyMenu.setConversionID(conversion_sessionId);
	    ontologyMenu.stopLoadingTimer();
	    graph.clearGraphData();
	    loadingModule.setBusyMode();
	    loadingModule.showLoadingIndicator();
	    loadingModule.collapseDetails();
	    missingImportsWarning = false;
	    d3.select("#loadingIndicator_closeButton").classed("hidden", true);
	    ontologyMenu.clearDetailInformation();
	  };
	  
	  /** ------------------ URL Interpreter -------------- **/
	  loadingModule.parseUrlAndLoadOntology = function ( storeCache ){
	    var autoStore = true;
	    if ( storeCache === false ) {
	      autoStore = false;
	    }
	    
	    graph.clearAllGraphData();
	    loadingModule.initializeLoader(autoStore);
	    var urlString = String(location);
	    var parameterArray = identifyParameter(urlString);
	    ontologyIdentifierFromURL = DEFAULT_JSON_NAME;
	    loadGraphOptions(parameterArray); // identifies and loads configuration values
	    var loadingMethod = identifyOntologyLoadingMethod(ontologyIdentifierFromURL);
	    d3.select("#progressBarValue").node().innerHTML = " ";
	    switch ( loadingMethod ) {
	      case 0:
	        loadingModule.from_presetOntology(ontologyIdentifierFromURL);
	        break;
	      case 1:
	        loadingModule.from_FileUpload(ontologyIdentifierFromURL);
	        break;
	      case 2:
	        loadingModule.from_JSON_URL(ontologyIdentifierFromURL);
	        break;
	      case 3:
	        loadingModule.from_IRI_URL(ontologyIdentifierFromURL);
	        break;
	      default:
	        console.log("Could not identify loading method , or not IMPLEMENTED YET");
	    }
	  };
	  
	  /** ------------------- LOADING --------------------- **/
	  // the loading module splits into 3 branches
	  // 1] PresetOntology Loading
	  // 2] File Upload
	  // 3] Load From URL / IRI
	  
	  loadingModule.from_JSON_URL = function ( fileName ){
	    var filename = decodeURIComponent(fileName.slice("url=".length));
	    ontologyIdentifierFromURL = filename;
	    
	    var ontologyContent = "";
	    if ( ontologyMenu.cachedOntology(filename) ) {
	      ontologyMenu.append_bulletPoint("Loading already cached ontology: " + filename);
	      ontologyContent = ontologyMenu.cachedOntology(filename);
	      loadingWasSuccessFul = true; // cached Ontology should be true;
	      parseOntologyContent(ontologyContent);
	      
	    } else {
	      // involve the o2v conveter;
	      ontologyMenu.append_message("Retrieving ontology from JSON URL " + filename);
	      requestServerTimeStampForJSON_URL(ontologyMenu.callbackLoad_JSON_FromURL, ["read?json=" + filename, filename]);
	    }
	  };
	  
	  function requestServerTimeStampForJSON_URL( callback, parameter ){
	    d3.xhr("serverTimeStamp", "application/text", function ( error, request ){
	      if ( error ) {
	        // could not get server timestamp -> no connection to owl2vowl
	        ontologyMenu.append_bulletPoint("Could not establish connection to OWL2VOWL service");
	        fallbackForJSON_URL(callback, parameter);
	      } else {
	        conversion_sessionId = request.responseText;
	        ontologyMenu.setConversionID(conversion_sessionId);
	        parameter.push(conversion_sessionId);
	        callback(parameter);
	      }
	    });
	    
	  }
	  
	  loadingModule.requestServerTimeStampForDirectInput = function ( callback, text ){
	    d3.xhr("serverTimeStamp", "application/text", function ( error, request ){
	      if ( error ) {
	        // could not get server timestamp -> no connection to owl2vowl
	        ontologyMenu.append_bulletPoint("Could not establish connection to OWL2VOWL service");
	        loadingModule.setErrorMode();
	        ontologyMenu.append_message_toLastBulletPoint("<br><span style='color:red'>Could not connect to OWL2VOWL service </span>");
	        loadingModule.showErrorDetailsMessage();
	        d3.select("#progressBarValue").style("width", "0%");
	        d3.select("#progressBarValue").classed("busyProgressBar", false);
	        d3.select("#progressBarValue").text("0%");
	        
	      } else {
	        conversion_sessionId = request.responseText;
	        ontologyMenu.setConversionID(conversion_sessionId);
	        callback(text, ["conversionID" + conversion_sessionId, conversion_sessionId]);
	      }
	    });
	  };
	  
	  loadingModule.from_IRI_URL = function ( fileName ){
	    // owl2vowl converters the given ontology url and returns json file;
	    var filename = decodeURIComponent(fileName.slice("iri=".length));
	    ontologyIdentifierFromURL = filename;
	    
	    var ontologyContent = "";
	    if ( ontologyMenu.cachedOntology(filename) ) {
	      ontologyMenu.append_bulletPoint("Loading already cached ontology: " + filename);
	      ontologyContent = ontologyMenu.cachedOntology(filename);
	      loadingWasSuccessFul = true; // cached Ontology should be true;
	      parseOntologyContent(ontologyContent);
	    } else {
	      // involve the o2v conveter;
	      var encoded = encodeURIComponent(filename);
	      ontologyMenu.append_bulletPoint("Retrieving ontology from IRI: " + filename);
	      requestServerTimeStampForIRI_Converte(ontologyMenu.callbackLoad_Ontology_FromIRI, ["convert?iri=" + encoded, filename]);
	    }
	  };
	  
	  loadingModule.fromFileDrop = function ( fileName, file ){
	    d3.select("#progressBarValue").node().innerHTML = " ";
	    loadingModule.initializeLoader(false);
	    
	    ontologyMenu.append_bulletPoint("Retrieving ontology from dropped file: " + fileName);
	    var ontologyContent = "";
	    
	    // two options here
	    //1] Direct Json Upload
	    if ( fileName.match(/\.json$/) ) {
	      ontologyMenu.setConversionID(-10000);
	      var reader = new FileReader();
	      reader.readAsText(file);
	      reader.onload = function (){
	        ontologyContent = reader.result;
	        ontologyIdentifierFromURL = fileName;
	        parseOntologyContent(ontologyContent);
	      };
	    } else {
	      //2] File Upload to OWL2VOWL Converter
	      // 1) check if we can get a timeStamp;
	      var parameterArray = [file, fileName];
	      requestServerTimeStamp(ontologyMenu.callbackLoadFromOntology, parameterArray);
	    }
	  };
	  
	  
	  loadingModule.from_FileUpload = function ( fileName ){
	    loadingModule.setBusyMode();
	    var filename = decodeURIComponent(fileName.slice("file=".length));
	    ontologyIdentifierFromURL = filename;
	    var ontologyContent = "";
	    if ( ontologyMenu.cachedOntology(filename) ) {
	      ontologyMenu.append_bulletPoint("Loading already cached ontology: " + filename);
	      ontologyContent = ontologyMenu.cachedOntology(filename);
	      loadingWasSuccessFul = true; // cached Ontology should be true;
	      parseOntologyContent(ontologyContent);
	      
	    } else {
	      // d3.select("#currentLoadingStep").node().innerHTML="Loading ontology from file "+ filename;
	      ontologyMenu.append_bulletPoint("Retrieving ontology from file: " + filename);
	      // get the file
	      var selectedFile = d3.select("#file-converter-input").property("files")[0];
	      // No selection -> this was triggered by the iri. Unequal names -> reuploading another file
	      if ( !selectedFile || (filename && (filename !== selectedFile.name)) ) {
	        ontologyMenu.append_message_toLastBulletPoint("<br><span style=\"color:red;\">No cached version of \"" + filename + "\" was found.</span><br>Please reupload the file.");
	        loadingModule.setErrorMode();
	        d3.select("#progressBarValue").classed("busyProgressBar", false);
	        graph.handleOnLoadingError();
	        return;
	      } else {
	        filename = selectedFile.name;
	      }
	
	
	// two options here
	//1] Direct Json Upload
	      if ( filename.match(/\.json$/) ) {
	        ontologyMenu.setConversionID(-10000);
	        var reader = new FileReader();
	        reader.readAsText(selectedFile);
	        reader.onload = function (){
	          ontologyContent = reader.result;
	          ontologyIdentifierFromURL = filename;
	          parseOntologyContent(ontologyContent);
	        };
	      } else {
	//2] File Upload to OWL2VOWL Converter
	        // 1) check if we can get a timeStamp;
	        var parameterArray = [selectedFile, filename];
	        requestServerTimeStamp(ontologyMenu.callbackLoadFromOntology, parameterArray);
	      }
	    }
	  };
	  
	  function fallbackForJSON_URL( callback, parameter ){
	    ontologyMenu.append_message_toLastBulletPoint("<br>Trying to convert with other communication protocol.");
	    callback(parameter);
	    
	  }
	  
	  function fallbackConversion( parameter ){
	    ontologyMenu.append_message_toLastBulletPoint("<br>Trying to convert with other communication protocol.");
	    var file = parameter[0];
	    var name = parameter[1];
	    var formData = new FormData();
	    formData.append("ontology", file);
	    
	    var xhr = new XMLHttpRequest();
	    xhr.open("POST", "convert", true);
	    var ontologyContent = "";
	    xhr.onload = function (){
	      if ( xhr.status === 200 ) {
	        ontologyContent = xhr.responseText;
	        ontologyMenu.setCachedOntology(name, ontologyContent);
	        ontologyIdentifierFromURL = name;
	        missingImportsWarning = true; // using this variable for warnings
	        ontologyMenu.append_message_toLastBulletPoint("<br>Success, <span style='color:yellow'>but you are using a deprecated OWL2VOWL service!<span>");
	        parseOntologyContent(ontologyContent);
	      }
	    };
	    
	    // check what this thing is doing;
	    xhr.onreadystatechange = function (){
	      if ( xhr.readyState === 4 && xhr.status === 0 ) {
	        ontologyMenu.append_message_toLastBulletPoint("<br>Old protocol also failed to establish connection to OWL2VOWL service!");
	        loadingModule.setErrorMode();
	        ontologyMenu.append_bulletPoint("Failed to load ontology");
	        ontologyMenu.append_message_toLastBulletPoint("<br><span style='color:red'>Could not connect to OWL2VOWL service </span>");
	        loadingModule.showErrorDetailsMessage();
	      }
	    };
	    xhr.send(formData);
	  }
	  
	  function requestServerTimeStampForIRI_Converte( callback, parameterArray ){
	    d3.xhr("serverTimeStamp", "application/text", function ( error, request ){
	      loadingModule.setBusyMode();
	      if ( error ) {
	        // could not get server timestamp -> no connection to owl2vowl
	        ontologyMenu.append_bulletPoint("Could not establish connection to OWL2VOWL service");
	        loadingModule.setErrorMode();
	        ontologyMenu.append_bulletPoint("Failed to load ontology");
	        ontologyMenu.append_message_toLastBulletPoint("<br><span style='color:red'>Could not connect to OWL2VOWL service </span>");
	        loadingModule.showErrorDetailsMessage();
	      } else {
	        conversion_sessionId = request.responseText;
	        ontologyMenu.setConversionID(conversion_sessionId);
	        // update paramater for new communication paradigm
	        parameterArray[0] = parameterArray[0] + "&sessionId=" + conversion_sessionId;
	        parameterArray.push(conversion_sessionId);
	        callback(parameterArray);
	      }
	    });
	  }
	  
	  function requestServerTimeStamp( callback, parameterArray ){
	    d3.xhr("serverTimeStamp", "application/text", function ( error, request ){
	      if ( error ) {
	        // could not get server timestamp -> no connection to owl2vowl
	        ontologyMenu.append_bulletPoint("Could not establish connection to OWL2VOWL service");
	        fallbackConversion(parameterArray); // tries o2v version0.3.4 communication
	      } else {
	        conversion_sessionId = request.responseText;
	        ontologyMenu.setConversionID(conversion_sessionId);
	        console.log("Request Session ID:" + conversion_sessionId);
	        callback(parameterArray[0], parameterArray[1], conversion_sessionId);
	      }
	    });
	  }
	  
	  loadingModule.directInput = function ( text ){
	    ontologyMenu.clearDetailInformation();
	    parseOntologyContent(text);
	  };
	  
	  loadingModule.loadFromOWL2VOWL = function ( ontoContent, filename ){
	    loadingWasSuccessFul = false;
	    
	    var old = d3.select("#bulletPoint_container").node().innerHTML;
	    if ( old.indexOf("(with warnings)") !== -1 ) {
	      missingImportsWarning = true;
	    }
	    
	    if ( ontologyMenu.cachedOntology(ontoContent) ) {
	      ontologyMenu.append_bulletPoint("Loading already cached ontology: " + filename);
	      parseOntologyContent(ontoContent);
	    } else { // set parse the ontology content;
	      parseOntologyContent(ontoContent);
	    }
	  };
	  
	  loadingModule.from_presetOntology = function ( selectedOntology ){
	    ontologyMenu.append_bulletPoint("Retrieving ontology: " + selectedOntology);
	    loadPresetOntology(selectedOntology);
	  };
	  
	  function loadPresetOntology( ontology ){
	    // check if already cached in ontology menu?
	    var f2r;
	    if ( ontology.indexOf("new_ontology") !== -1 ) {
	      loadingModule.hideLoadingIndicator();
	      graph.showEditorHintIfNeeded();
	      f2r = "./data/new_ontology.json";
	      
	    }
	    
	    loadingWasSuccessFul = false;
	    var ontologyContent = "";
	    if ( ontologyMenu.cachedOntology(ontology) ) {
	      ontologyMenu.append_bulletPoint("Loading already cached ontology: " + ontology);
	      ontologyContent = ontologyMenu.cachedOntology(ontology);
	      loadingWasSuccessFul = true; // cached Ontology should be true;
	      loadingModule.showLoadingIndicator();
	      parseOntologyContent(ontologyContent);
	      
	    } else {
	      // read the file name
	      
	      var fileToRead = "./data/" + ontology + ".json";
	      if ( f2r ) {
	        fileToRead = f2r;
	      } // overwrite the newOntology Index
	      // read file
	      d3.xhr(fileToRead, "application/json", function ( error, request ){
	        var loadingSuccessful = !error;
	        if ( loadingSuccessful ) {
	          ontologyContent = request.responseText;
	          parseOntologyContent(ontologyContent);
	        } else {
	          // some error occurred
	          ontologyMenu.append_bulletPoint("Failed to load: " + ontology);
	          ontologyMenu.append_message_toLastBulletPoint(" <span style='color: red'>ERROR STATUS:</span> " + error.status);
	          graph.handleOnLoadingError();
	          loadingModule.setErrorMode();
	        }
	      });
	    }
	  }
	  
	  /** -- OLE Customization -- **/
			
	  loadingModule.parseOntologyContent = function(content){
	    // loadingModule.initializeLoader(autoStore);
	    graph.clearAllGraphData();
	    loadingModule.initializeLoader();
	    var urlString = String(location);
	    var parameterArray = identifyParameter(urlString);
	    loadGraphOptions(parameterArray); // identifies and loads configuration values
	    ontologyMenu.append_bulletPoint("Reading ontology graph ... ");
	    var _loader = ontologyMenu.getLoadingFunction();
	    _loader(content, ontologyIdentifierFromURL, "noAlternativeNameYet", true);
	  }
	
	  /** -- END -- **/
	  
	  /** -- PARSE JSON CONTENT -- **/
	  function parseOntologyContent( content ){
	    
	    ontologyMenu.append_bulletPoint("Reading ontology graph ... ");
	    var _loader = ontologyMenu.getLoadingFunction();
	    _loader(content, ontologyIdentifierFromURL, "noAlternativeNameYet");
	  }
	  
	  loadingModule.notValidJsonFile = function (){
	    graph.clearGraphData();
	    ontologyMenu.append_message_toLastBulletPoint(" <span style='color:red;'>failed</span>");
	    ontologyMenu.append_message_toLastBulletPoint("<br><span style='color:red;'>Error: Received empty graph</span>");
	    loadingWasSuccessFul = false;
	    graph.handleOnLoadingError();
	    
	  };
	  
	  loadingModule.validJsonFile = function (){
	    ontologyMenu.append_message_toLastBulletPoint("done");
	    loadingWasSuccessFul = true;
	  };
	  
	  
	  /** --- HELPER FUNCTIONS **/
	  
	  function identifyParameter( url ){
	    var numParameters = (url.match(/#/g) || []).length;
	    // create parameters array
	    var paramArray = [];
	    if ( numParameters > 0 ) {
	      var tokens = url.split("#");
	      // skip the first token since it is the address of the server
	      for ( var i = 1; i < tokens.length; i++ ) {
	        if ( tokens[i].length === 0 ) {
	          // this token belongs actually to the last paramArray
	          paramArray[paramArray.length - 1] = paramArray[paramArray.length - 1] + "#";
	        } else {
	          paramArray.push(tokens[i]);
	        }
	      }
	    }
	    return paramArray;
	  }
	  
	  
	  function loadGraphOptions( parameterArray ){
	    var optString = "opts=";
	    
	    function loadDefaultConfig(){
	      graph.options().setOptionsFromURL(graph.options().defaultConfig(), false);
	    }
	    
	    function loadCustomConfig( opts ){
	      var changeEditingFlag = false;
	      var defObj = graph.options().defaultConfig();
	      for ( var i = 0; i < opts.length; i++ ) {
	        var keyVal = opts[i].split('=');
	        if ( keyVal[0] === "editorMode" ) {
	          changeEditingFlag = true;
	        }
	        defObj[keyVal[0]] = keyVal[1];
	      }
	      graph.options().setOptionsFromURL(defObj, changeEditingFlag);
	    }
	    
	    function identifyOptions( paramArray ){
	      if ( paramArray[0].indexOf(optString) >= 0 ) {
	        // parse the parameters;
	        var parameterLength = paramArray[0].length;
	        var givenOptionsStr = paramArray[0].substr(5, parameterLength - 6);
	        var optionsArray = givenOptionsStr.split(';');
	        loadCustomConfig(optionsArray);
	      } else {
	        ontologyIdentifierFromURL = paramArray[0];
	        loadDefaultConfig();
	      }
	    }
	    
	    function identifyOptionsAndOntology( paramArray ){
	      
	      if ( paramArray[0].indexOf(optString) >= 0 ) {
	        // parse the parameters;
	        var parameterLength = paramArray[0].length;
	        var givenOptionsStr = paramArray[0].substr(5, parameterLength - 6);
	        var optionsArray = givenOptionsStr.split(';');
	        loadCustomConfig(optionsArray);
	      } else {
	        loadDefaultConfig();
	      }
	      ontologyIdentifierFromURL = paramArray[1];
	    }
	    
	    switch ( parameterArray.length ) {
	      case 0:
	        loadDefaultConfig();
	        break;
	      case 1:
	        identifyOptions(parameterArray);
	        break;
	      case 2:
	        identifyOptionsAndOntology(parameterArray);
	        break;
	      default :
	        console.log("To many input parameters , loading default config");
	        loadDefaultConfig();
	        ontologyIdentifierFromURL = "ERROR_TO_MANY_INPUT_PARAMETERS";
	    }
	  }
	  
	  
	  function identifyOntologyLoadingMethod( url ){
	    var iriKey = "iri=";
	    var urlKey = "url=";
	    var fileKey = "file=";
	    
	    var method = -1;
	    if ( url.substr(0, fileKey.length) === fileKey ) {
	      method = FILE_UPLOAD;
	    } else if ( url.substr(0, urlKey.length) === urlKey ) {
	      method = JSON_URL;
	    } else if ( url.substr(0, iriKey.length) === iriKey ) {
	      method = IRI_URL;
	    } else {
	      method = PREDEFINED;
	    }
	    return method;
	  }
	  
	  return loadingModule;
	}
	;
	
	
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 342:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {module.exports = function ( graph ){
	  /** variable defs **/
	  var warningModule = {};
	  var superContainer = d3.select("#WarningErrorMessages");
	  var _messageContainers = [];
	  var _messageContext = [];
	  var _visibleStatus = [];
	  
	  var _filterHintId;
	  var _editorHintId;
	  var _messageId = -1;
	  superContainer.style("display", "inline-block");
	  var cssStyleIndex = 0;
	  var styleSelectorIndex = 1;
	  
	  
	  // helper for standalone webvowl in chrome
	  function createCSSSelector( name, rules ){
	    var style = document.createElement('style');
	    style.type = 'text/css';
	    document.getElementsByTagName('head')[0].appendChild(style);
	    if ( !(style.sheet || {}).insertRule )
	      (style.styleSheet || style.sheet).addRule(name, rules);
	    else
	      style.sheet.insertRule(name + "{" + rules + "}", 0);
	  }
	  
	  
	  function findCSS_Index(){
	    var css = document.styleSheets[styleSelectorIndex].cssRules;
	    
	    if ( css === null ) {
	      styleSelectorIndex = 2;
	      // using chrome local css >> create own internal style for animation end
	      createCSSSelector("@keyframes msg_CollapseAnimation", " 0% { top: 0; } 100% { top: -400px;}");
	      css = document.styleSheets[styleSelectorIndex].cssRules;
	    }
	    
	    if ( css ) {
	      for ( var i = 0; i < css.length; i++ ) {
	        var entry = css[i];
	        if ( entry.name === "msg_CollapseAnimation" ) {
	          cssStyleIndex = i;
	        }
	      }
	    }
	    
	  }
	  
	  findCSS_Index();
	  
	  warningModule.addMessageBox = function (){
	    
	    // add a container;
	    _messageId++;
	    var messageContainer = d3.select("#WarningErrorMessages").append("div");
	    messageContainer.node().id = "messageContainerId_" + _messageId;
	    
	    var messageContext = messageContainer.append("div");
	    messageContext.node().id = "messageContextId_" + _messageId;
	    messageContext.style("top", "0");
	    messageContainer.style("position", "relative");
	    messageContainer.style("width", "100%");
	    //save in array
	    _messageContainers.push(messageContainer);
	    _messageContext.push(messageContext);
	    
	    // add animation to the container
	    messageContainer.node().addEventListener("animationend", _msgContainer_animationEnd);
	    
	    // set visible flag that is used in end of animation
	    _visibleStatus[_messageId] = true;
	    return _messageId;
	  };
	  
	  function _msgContainer_animationEnd(){
	    var containerId = this.id;
	    var tokens = containerId.split("_")[1];
	    var mContainer = d3.select("#" + containerId);
	    // get number of children
	    mContainer.classed("hidden", !_visibleStatus[tokens]);
	    // clean up DOM
	    if ( !_visibleStatus[tokens] ) {
	      mContainer.remove();
	      _messageContext[tokens] = null;
	      _messageContainers[tokens] = null;
	    }
	    // remove event listener
	    var c = d3.select(this);
	    // c.node().removeEventListener("animationend",_msgContainer_animationEnd);
	  }
	  
	  warningModule.createMessageContext = function ( id ){
	    var warningContainer = _messageContext[id];
	    var moduleContainer = _messageContainers[id];
	    var generalHint = warningContainer.append('div');
	    generalHint.node().innerHTML = "";
	    _editorHintId = id;
	    /** Editing mode activated. You can now modify an existing ontology or create a new one via the <em>ontology</em> menu. You can save any ontology using the <em>export</em> menu (and exporting it as TTL file).**/
	    generalHint.node().innerHTML += "Editing mode activated.<br>" +
	      "You can now modify an existing ontology or create a new one via the <em>ontology</em> menu.<br>" +
	      "You can save any ontology using the <em>export</em> menu (and exporting it as TTL file).";
	    
	    generalHint.style("padding", "5px");
	    generalHint.style("line-height", "1.2em");
	    generalHint.style("font-size", "1.2em");
	    
	    
	    var ul = warningContainer.append('ul');
	    ul.append('li').node().innerHTML = "Create a class with <b>double click / tap</b> on empty canvas area.";
	    ul.append('li').node().innerHTML = "Edit names with <b>double click / tap</b> on element.</li>";
	    ul.append('li').node().innerHTML = "Selection of default constructors is provided in the left sidebar.";
	    ul.append('li').node().innerHTML = "Additional editing functionality is provided in the right sidebar.";
	    
	    
	    var gotItButton = warningContainer.append("label");
	    gotItButton.node().id = "killWarningErrorMessages_" + id;
	    gotItButton.node().innerHTML = "Got It";
	    gotItButton.on("click", warningModule.closeMessage);
	    
	    moduleContainer.classed("hidden", false);
	    moduleContainer.style("-webkit-animation-name", "warn_ExpandAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	  };
	  
	  warningModule.showMessage = function ( id ){
	    var moduleContainer = _messageContainers[id];
	    moduleContainer.classed("hidden", false);
	    moduleContainer.style("-webkit-animation-name", "warn_ExpandAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	  };
	  
	  warningModule.closeMessage = function ( id ){
	    var nId;
	    if ( id === undefined ) {
	      var givenId = this.id;
	      nId = givenId.split("_")[1];
	    } else {
	      nId = id;
	    }
	    if ( id && id.indexOf("_") !== -1 ) {
	      nId = id.split("_")[1];
	    }
	    _visibleStatus[nId] = false;
	    // get module;
	    var moduleContainer = _messageContainers[nId];
	    moduleContainer.style("-webkit-animation-name", "warn_CollapseAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	    
	    var m_height = moduleContainer.node().getBoundingClientRect().height;
	    
	    // find my id in the children
	    var pNode = moduleContainer.node().parentNode;
	    
	    var followingChildren = [];
	    var pChild = pNode.children;
	    var pChild_len = pChild.length;
	    var containerId = moduleContainer.node().id;
	    var found_me = false;
	    for ( var i = 0; i < pChild_len; i++ ) {
	      if ( found_me === true ) {
	        followingChildren.push(pChild[i].id);
	      }
	      
	      if ( containerId === pChild[i].id ) {
	        found_me = true;
	      }
	    }
	    
	    for ( var fc = 0; fc < followingChildren.length; fc++ ) {
	      var child = d3.select("#" + followingChildren[fc]);
	      // get the document style and overwrite it;
	      var superCss = document.styleSheets[styleSelectorIndex].cssRules[cssStyleIndex];
	      // remove the existing 0% and 100% rules
	      superCss.deleteRule("0%");
	      superCss.deleteRule("100%");
	      
	      superCss.appendRule("0%   {top: 0;}");
	      superCss.appendRule("100% {top: -" + m_height + "px;");
	      
	      child.style("-webkit-animation-name", "msg_CollapseAnimation");
	      child.style("-webkit-animation-duration", "0.5s");
	      child.node().addEventListener("animationend", _child_animationEnd);
	    }
	  };
	  
	  function _child_animationEnd(){
	    var c = d3.select(this);
	    c.style("-webkit-animation-name", "");
	    c.style("-webkit-animation-duration", "");
	    c.node().removeEventListener("animationend", _child_animationEnd);
	  }
	  
	  warningModule.closeFilterHint = function (){
	    if ( _messageContainers[_filterHintId] ) {
	      _messageContainers[_filterHintId].classed("hidden", true);
	      _messageContainers[_filterHintId].remove();
	      _messageContainers[_filterHintId] = null;
	      _messageContext[_filterHintId] = null;
	      _visibleStatus[_filterHintId] = false;
	    }
	  };
	  
	  warningModule.showEditorHint = function (){
	    var id = warningModule.addMessageBox();
	    warningModule.createMessageContext(id);
	  };
	  
	  
	  warningModule.responseWarning = function ( header, reason, action, callback, parameterArray, forcedWarning ){
	    var id = warningModule.addMessageBox();
	    var warningContainer = _messageContext[id];
	    var moduleContainer = _messageContainers[id];
	    _visibleStatus[id] = true;
	    d3.select("#blockGraphInteractions").classed("hidden", false);
	    var graphWidth = 0.5 * graph.options().width();
	    
	    if ( header.length > 0 ) {
	      var head = warningContainer.append("div");
	      head.style("padding", "5px");
	      var titleHeader = head.append("div");
	      // some classes
	      titleHeader.style("display", "inline-flex");
	      titleHeader.node().innerHTML = "<b>Warning:</b>";
	      titleHeader.style("padding-right", "3px");
	      var msgHeader = head.append("div");
	      // some classes
	      msgHeader.style("display", "inline-flex");
	      msgHeader.style("max-width", graphWidth + "px");
	      
	      msgHeader.node().innerHTML = header;
	    }
	    if ( reason.length > 0 ) {
	      var reasonContainer = warningContainer.append("div");
	      reasonContainer.style("padding", "5px");
	      var reasonHeader = reasonContainer.append("div");
	      // some classes
	      reasonHeader.style("display", "inline-flex");
	      reasonHeader.style("padding-right", "3px");
	      
	      reasonHeader.node().innerHTML = "<b>Reason:</b>";
	      var msgReason = reasonContainer.append("div");
	      // some classes
	      msgReason.style("display", "inline-flex");
	      msgReason.style("max-width", graphWidth + "px");
	      msgReason.node().innerHTML = reason;
	    }
	    if ( action.length > 0 ) {
	      var actionContainer = warningContainer.append("div");
	      actionContainer.style("padding", "5px");
	      var actionHeader = actionContainer.append("div");
	      // some classes
	      actionHeader.style("display", "inline-flex");
	      actionHeader.style("padding-right", "8px");
	      actionHeader.node().innerHTML = "<b>Action:</b>";
	      var msgAction = actionContainer.append("div");
	      // some classes
	      msgAction.style("display", "inline-flex");
	      msgAction.style("max-width", graphWidth + "px");
	      msgAction.node().innerHTML = action;
	    }
	    
	    var gotItButton = warningContainer.append("label");
	    gotItButton.node().id = "killWarningErrorMessages_" + id;
	    gotItButton.node().innerHTML = "Continue";
	    gotItButton.on("click", function (){
	      warningModule.closeMessage(this.id);
	      d3.select("#blockGraphInteractions").classed("hidden", true);
	      callback(parameterArray[0], parameterArray[1], parameterArray[2], parameterArray[3]);
	    });
	    warningContainer.append("span").node().innerHTML = "|";
	    var cancelButton = warningContainer.append("label");
	    cancelButton.node().id = "cancelButton_" + id;
	    cancelButton.node().innerHTML = "Cancel";
	    cancelButton.on("click", function (){
	      warningModule.closeMessage(this.id);
	      d3.select("#blockGraphInteractions").classed("hidden", true);
	    });
	    moduleContainer.classed("hidden", false);
	    moduleContainer.style("-webkit-animation-name", "warn_ExpandAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	  };
	  
	  warningModule.showFilterHint = function (){
	    var id = warningModule.addMessageBox();
	    var warningContainer = _messageContext[id];
	    var moduleContainer = _messageContainers[id];
	    _visibleStatus[id] = true;
	    
	    _filterHintId = id;
	    var generalHint = warningContainer.append('div');
	    /** Editing mode activated. You can now modify an existing ontology or create a new one via the <em>ontology</em> menu. You can save any ontology using the <em>export</em> menu (and exporting it as TTL file).**/
	    generalHint.node().innerHTML = "Collapsing filter activated.<br>" +
	      "The number of visualized elements has been automatically reduced.<br>" +
	      "Use the degree of collapsing slider in the <em>filter</em> menu to adjust the visualization.<br><br>" +
	      "<em>Note:</em> A performance decrease could be experienced with a growing amount of visual elements in the graph.";
	    
	    
	    generalHint.style("padding", "5px");
	    generalHint.style("line-height", "1.2em");
	    generalHint.style("font-size", "1.2em");
	    
	    var gotItButton = warningContainer.append("label");
	    gotItButton.node().id = "killFilterMessages_" + id;
	    gotItButton.node().innerHTML = "Got It";
	    gotItButton.on("click", warningModule.closeMessage);
	    
	    moduleContainer.classed("hidden", false);
	    moduleContainer.style("-webkit-animation-name", "warn_ExpandAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	  };
	  
	  warningModule.showMultiFileUploadWarning = function (){
	    var id = warningModule.addMessageBox();
	    var warningContainer = _messageContext[id];
	    var moduleContainer = _messageContainers[id];
	    _visibleStatus[id] = true;
	    
	    _filterHintId = id;
	    var generalHint = warningContainer.append('div');
	    
	    generalHint.node().innerHTML = "Uploading multiple files is not supported.<br>";
	    
	    generalHint.style("padding", "5px");
	    generalHint.style("line-height", "1.2em");
	    generalHint.style("font-size", "1.2em");
	    
	    var gotItButton = warningContainer.append("label");
	    gotItButton.node().id = "killFilterMessages_" + id;
	    gotItButton.node().innerHTML = "Got It";
	    gotItButton.on("click", warningModule.closeMessage);
	    
	    moduleContainer.classed("hidden", false);
	    moduleContainer.style("-webkit-animation-name", "warn_ExpandAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	  };
	  
	  warningModule.showWarning = function ( header, reason, action, type, forcedWarning, additionalOpts ){
	    var id = warningModule.addMessageBox();
	    var warningContainer = _messageContext[id];
	    var moduleContainer = _messageContainers[id];
	    _visibleStatus[id] = true;
	    
	    // add new one;
	    var graphWidth = 0.5 * graph.options().width();
	    
	    if ( header.length > 0 ) {
	      var head = warningContainer.append("div");
	      head.style("padding", "5px");
	      var titleHeader = head.append("div");
	      // some classes
	      titleHeader.style("display", "inline-flex");
	      titleHeader.node().innerHTML = "<b>Warning:</b>";
	      titleHeader.style("padding-right", "3px");
	      var msgHeader = head.append("div");
	      // some classes
	      msgHeader.style("display", "inline-flex");
	      msgHeader.style("max-width", graphWidth + "px");
	      
	      msgHeader.node().innerHTML = header;
	    }
	    if ( reason.length > 0 ) {
	      var reasonContainer = warningContainer.append("div");
	      reasonContainer.style("padding", "5px");
	      var reasonHeader = reasonContainer.append("div");
	      // some classes
	      reasonHeader.style("display", "inline-flex");
	      reasonHeader.style("padding-right", "3px");
	      
	      reasonHeader.node().innerHTML = "<b>Reason:</b>";
	      var msgReason = reasonContainer.append("div");
	      // some classes
	      msgReason.style("display", "inline-flex");
	      msgReason.style("max-width", graphWidth + "px");
	      msgReason.node().innerHTML = reason;
	    }
	    if ( action.length > 0 ) {
	      var actionContainer = warningContainer.append("div");
	      actionContainer.style("padding", "5px");
	      var actionHeader = actionContainer.append("div");
	      // some classes
	      actionHeader.style("display", "inline-flex");
	      actionHeader.style("padding-right", "8px");
	      actionHeader.node().innerHTML = "<b>Action:</b>";
	      var msgAction = actionContainer.append("div");
	      // some classes
	      msgAction.style("display", "inline-flex");
	      msgAction.style("max-width", graphWidth + "px");
	      msgAction.node().innerHTML = action;
	    }
	    
	    var gotItButton;
	    if ( type === 1 ) {
	      gotItButton = warningContainer.append("label");
	      gotItButton.node().id = "killWarningErrorMessages_" + id;
	      gotItButton.node().innerHTML = "Got It";
	      gotItButton.on("click", warningModule.closeMessage);
	    }
	    
	    if ( type === 2 ) {
	      gotItButton = warningContainer.append("label");
	      gotItButton.node().id = "killWarningErrorMessages_" + id;
	      gotItButton.node().innerHTML = "Got It";
	      gotItButton.on("click", warningModule.closeMessage);
	      warningContainer.append("span").node().innerHTML = "|";
	      var zoomToElementButton = warningContainer.append("label");
	      zoomToElementButton.node().id = "zoomElementThing_" + id;
	      zoomToElementButton.node().innerHTML = "Zoom to element ";
	      zoomToElementButton.on("click", function (){
	        // assume the additional Element is for halo;
	        graph.zoomToElementInGraph(additionalOpts);
	      });
	      warningContainer.append("span").node().innerHTML = "|";
	      var ShowElementButton = warningContainer.append("label");
	      ShowElementButton.node().id = "showElementThing_" + id;
	      ShowElementButton.node().innerHTML = "Indicate element";
	      ShowElementButton.on("click", function (){
	        // assume the additional Element is for halo;
	        if ( additionalOpts.halo() === false ) {
	          additionalOpts.drawHalo();
	          graph.updatePulseIds([additionalOpts.id()]);
	        } else {
	          additionalOpts.removeHalo();
	          additionalOpts.drawHalo();
	          graph.updatePulseIds([additionalOpts.id()]);
	        }
	      });
	    }
	    moduleContainer.classed("hidden", false);
	    moduleContainer.style("-webkit-animation-name", "warn_ExpandAnimation");
	    moduleContainer.style("-webkit-animation-duration", "0.5s");
	    moduleContainer.classed("hidden", false);
	  };
	  
	  return warningModule;
	};
	
	
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ }),

/***/ 343:
/***/ (function(module, exports, __webpack_require__) {

	/* WEBPACK VAR INJECTION */(function(d3) {module.exports = function ( graph ){
	  /** variable defs **/
	  var directInputModule = {};
	  var inputContainer = d3.select("#DirectInputContent");
	  inputContainer.style("top", "0");
	  inputContainer.style("position", "absolute");
	  var textArea = d3.select("#directInputTextArea");
	  var visibleContainer = false;
	  
	  inputContainer.style("border", "1px solid black");
	  inputContainer.style("padding", "5px");
	  inputContainer.style("background", "#fff");
	  
	  
	  // connect upload and close button;
	  directInputModule.handleDirectUpload = function (){
	    
	    var text = textArea.node().value;
	    var jsonOBJ;
	    try {
	      jsonOBJ = JSON.parse(text);
	      graph.options().loadingModule().directInput(text);
	      // close if successful
	      if ( jsonOBJ.class.length > 0 ) {
	        directInputModule.setDirectInputMode(false);
	      }
	    }
	    catch ( e ) {
	      try {
	        // Initialize;
	        graph.options().loadingModule().initializeLoader();
	        graph.options().loadingModule().requestServerTimeStampForDirectInput(
	          graph.options().ontologyMenu().callbackLoad_Ontology_From_DirectInput, text
	        );
	      } catch ( error2 ) {
	        console.log("Error " + error2);
	        d3.select("#Error_onLoad").classed("hidden", false);
	        d3.select("#Error_onLoad").node().innerHTML = "Failed to convert the input!";
	      }
	    }
	  };
	  
	  directInputModule.handleCloseButton = function (){
	    directInputModule.setDirectInputMode(false);
	  };
	  
	  directInputModule.updateLayout = function (){
	    var w = graph.options().width();
	    var h = graph.options().height();
	    textArea.style("width", 0.4 * w + "px");
	    textArea.style("height", 0.7 * h + "px");
	  };
	  
	  directInputModule.setDirectInputMode = function ( val ){
	    if ( !val ) {
	      visibleContainer = !visibleContainer;
	    }
	    else {
	      visibleContainer = val;
	    }
	    // update visibility;
	    directInputModule.updateLayout();
	    d3.select("#Error_onLoad").classed("hidden", true);
	    inputContainer.classed("hidden", !visibleContainer);
	  };
	  
	  
	  d3.select("#directUploadBtn").on("click", directInputModule.handleDirectUpload);
	  d3.select("#close_directUploadBtn").on("click", directInputModule.handleCloseButton);
	  
	  return directInputModule;
	};
	
	
	
	/* WEBPACK VAR INJECTION */}.call(exports, __webpack_require__(6)))

/***/ })

/******/ });
//# sourceMappingURL=webvowl.app.js.map