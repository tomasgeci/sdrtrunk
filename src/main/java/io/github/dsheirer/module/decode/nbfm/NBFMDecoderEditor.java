/*******************************************************************************
 *     SDR Trunk 
 *     Copyright (C) 2014-2016 Dennis Sheirer
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>
 ******************************************************************************/
package io.github.dsheirer.module.decode.nbfm;

import io.github.dsheirer.controller.channel.Channel;
import io.github.dsheirer.gui.editor.Editor;
import io.github.dsheirer.gui.editor.EditorValidationException;
import io.github.dsheirer.gui.editor.ValidatingEditor;
import io.github.dsheirer.module.decode.config.DecodeConfiguration;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;

public class NBFMDecoderEditor extends ValidatingEditor<Channel>
{
    private static final long serialVersionUID = 1L;
    
	public NBFMDecoderEditor()
	{
		init();
	}
	
	private void init()
	{
		setLayout( new MigLayout( "insets 0 0 0 0,wrap 2", "[right][grow,fill]", "" ) );
		add( new JLabel( "Narrow Band FM Decoder" ) );
	}

	@Override
	public void validate( Editor<Channel> editor ) throws EditorValidationException
	{
		//No validation
	}

	@Override
	public void save()
	{
		if( hasItem() && isModified() )
		{
			DecodeConfigNBFM nbfm = new DecodeConfigNBFM();
			getItem().setDecodeConfiguration( nbfm );
		}
		
		setModified( false );
	}

	private void setControlsEnabled( boolean enabled )
	{
	}

	@Override
	public void setItem( Channel item )
	{
		super.setItem( item );
		
		if( hasItem() )
		{
			setControlsEnabled( true );
			
			DecodeConfiguration config = getItem().getDecodeConfiguration();
			
			if( config instanceof DecodeConfigNBFM )
			{
				DecodeConfigNBFM nbfm = (DecodeConfigNBFM)config;
				setModified( false );
			}
			else
			{
				setModified( true );
			}
		}
		else
		{
			setControlsEnabled( false );
			setModified( false );
		}
	}
}
