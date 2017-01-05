
Ext.define('Ext.ux.form.NumericField', {
    extend: 'Ext.form.field.Number',
    alias: 'widget.currencyfield',
    
    currencySymbol: '₮',
    currencySymbolPos : 'left', 
    useThousandSeparator: true,
    thousandSeparator: ',',
    alwaysDisplayDecimals: true,
    fieldStyle: 'text-align: right;',
    hideTrigger: true,
    
    initComponent: function(){
        if (this.useThousandSeparator && this.decimalSeparator == ',' && this.thousandSeparator == ',') 
            this.thousandSeparator = '.';
        else 
            if (this.allowDecimals && this.thousandSeparator == '.' && this.decimalSeparator == '.') 
                this.decimalSeparator = ',';
        
        this.callParent(arguments);
    },
    setValue: function(value){
        Ext.ux.form.NumericField.superclass.setValue.call(this, value !=  null ? value.toString().replace('.', this.decimalSeparator) : value);
        
        this.setRawValue(this.getFormattedValue(this.getValue()));
    },
    getFormattedValue: function(value){
        if (Ext.isEmpty(value) || !this.hasFormat()) 
            return value;
        else 
        {
            var neg = null;
            
            value = (neg = value < 0) ? value * -1 : value;
            value = this.allowDecimals && this.alwaysDisplayDecimals ? value.toFixed(this.decimalPrecision) : value;
            
            if (this.useThousandSeparator) 
            {
                if (this.useThousandSeparator && Ext.isEmpty(this.thousandSeparator)) 
                    throw ('NumberFormatException: invalid thousandSeparator, property must has a valid character.');
                
                if (this.thousandSeparator == this.decimalSeparator) 
                    throw ('NumberFormatException: invalid  thousandSeparator, thousand separator must be different from  decimalSeparator.');
                
                value = value.toString();
                
                
                var ps = value.split('.');
                ps[1] = ps[1] ? ps[1] : null;
                
                var whole = ps[0];
                
                var r = /(\d+)(\d{3})/;
                
                var ts = this.thousandSeparator;
                
                while (r.test(whole)) 
                    whole = whole.replace(r, '$1' + ts + '$2');
                
                value = whole;
                
            }
            
            if (this.currencySymbolPos == 'right') {
                return Ext.String.format('{0}{1}{2}', (neg ? '-' : ''),  value, (Ext.isEmpty(this.currencySymbol) ? '' : ' ' +  this.currencySymbol));
            } else {
                return Ext.String.format('{0}{1}{2}', (neg ? '-'  : ''), (Ext.isEmpty(this.currencySymbol) ? '' : this.currencySymbol + '  '), value);
            }
        }
    },
    
    parseValue: function(value){
        return Ext.ux.form.NumericField.superclass.parseValue.call(this, this.removeFormat(value));
    },
    
    removeFormat: function(value){
        if (Ext.isEmpty(value) || !this.hasFormat()) 
            return value;
        else 
        {
            if (this.currencySymbolPos == 'right') {
                value = value.toString().replace(' ' + this.currencySymbol, '');
            } else {
                value = value.toString().replace(this.currencySymbol + ' ', '');
            }
            
            value = this.useThousandSeparator ? value.replace(new RegExp('[' + this.thousandSeparator + ']', 'g'), '') : value;
            
            return value;
        }
    },
    
    getErrors: function(value){
        return Ext.ux.form.NumericField.superclass.getErrors.call(this, this.removeFormat(value));
    },
    hasFormat: function(){
        return this.decimalSeparator != '.' ||  (this.useThousandSeparator == true && this.getRawValue() !=  null) || !Ext.isEmpty(this.currencySymbol) ||  this.alwaysDisplayDecimals;
    },
    
    listeners:{
      'change':function(){
          val=this.getFormattedValue(this.parseValue(this.getRawValue()));
          this.setValue(val);
      }  
    },
    processRawValue: function(value) {
        return this.removeFormat(value);
    }
});