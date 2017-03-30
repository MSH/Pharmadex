/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or http://ckeditor.com/license
 * 
 * this configuration resolves "body p" problem AK 20170329
 * Just put to <pe:ckEditor customConfig="#{request.contextPath}/resources/js/CKEconfig.js"
 */

CKEDITOR.editorConfig = function( config ) {
	config.removePlugins = 'elementspath';
	config.resize_enabled = false;
};
