Ext.define('OSS.App', {
    extend: 'Ext.ux.desktop.App',

    init: function() {
        this.callParent();
        
        Ext.override(Ext.view.AbstractView, {
            onRender: function() {
                var me = this;                
                this.callOverridden();                
                if (me.mask && Ext.isObject(me.store)) {
                    me.setMaskBind(me.store);
                }
            }
        });                 
    },
    
    desktopCfg: function() {
		var me = this;
		return { 
            contextMenuItems: [
                { text: 'Wallpaper', handler: me.onSettings, scope: me }
            ],
            shortcuts: Ext.create('Ext.data.Store', {
                model: 'Ext.ux.desktop.ShortcutModel',
                data: [                       
                    { name: Ext.sfa.translate_arrays[langid][501], iconCls: 'grid-shortcut', hidden: hidden_values['sale-module-win'], module: 'sale-module-win' },                                        
                    { name: Ext.sfa.translate_arrays[langid][509], iconCls: 'storage_desktop', hidden: hidden_values['storage-module-win'], module: 'storage-module-win' },
                    { name: Ext.sfa.translate_arrays[langid][507], iconCls: 'lease_desktop', hidden: hidden_values['lease-module-win'], module: 'lease-module-win' },
                    { name: Ext.sfa.translate_arrays[langid][514], iconCls:'map-shortcut', hidden: hidden_values['google-map-win'], module:'google-map-win'},              
                    { name: Ext.sfa.translate_arrays[langid][524], iconCls:'report-shortcut', hidden: hidden_values['report-module-win'], module:'report-module-win'}                                       
                ]
            }),
            wallpaper: 'images/wallpapers/ios_7_galaxy-wide.jpg',
            wallpaperStretch: true
        };
	},
	
	getExtendModules : function() {
		return [new OSS.AllReports()];
	},
	
    getStartConfig : function() {
        var me = this, ret = me.callParent();        
        return Ext.apply(ret, me.startBarCfg());
    },

    getDesktopConfig: function () {
        var me = this, ret = me.callParent();
        
        return Ext.apply(ret, me.desktopCfg());
    },
    
    getTaskbarConfig: function () {
        var ret = this.callParent();

        return Ext.apply(ret, this.taskBarCfg());
    }
});