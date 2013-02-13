package gui.swing;

import gui.swing.test.TestVideoCanvasFrameDisplay;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import variable.booleanv.BooleanVariable;
import variable.doublev.DoubleInputVariableInterface;
import variable.doublev.DoubleOutputVariableInterface;
import variable.doublev.DoubleVariable;

public class JButtonBoolean extends JButton
{
	private final JButtonBoolean mThis;
	private BooleanVariable mBooleanVariable;
	private String mOnLabel, mOffLabel;

	public JButtonBoolean(final boolean pInitialState,
												String pOnLabel,
												String pOffLabel)
	{
		mThis = this;
		mBooleanVariable = new BooleanVariable(pInitialState);
		mOnLabel = pOnLabel;
		mOffLabel = pOffLabel;
		setLabelFromState(mBooleanVariable.getBooleanValue());

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent pE)
			{
				mBooleanVariable.toggle(mThis);
				
				final boolean lButtonState = mBooleanVariable.getBooleanValue();
				//System.out.println(lButtonState);

				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						try
						{
							setLabelFromState(lButtonState);
						}
						catch (Throwable e)
						{
							e.printStackTrace();
						}
					}
				});
			}

		});

		mBooleanVariable.sendUpdatesTo(new DoubleInputVariableInterface()
		{

			@Override
			public void setValue(Object pDoubleEventSource, double pNewValue)
			{
				final boolean lButtonState = BooleanVariable.double2boolean(pNewValue);
				if(pDoubleEventSource!=mThis)
				{
					EventQueue.invokeLater(new Runnable()
					{
						public void run()
						{
							try
							{
								setLabelFromState(lButtonState);
							}
							catch (Throwable e)
							{
								e.printStackTrace();
							}
						}
					});
				}
				
			}
		});/**/

	}

	public BooleanVariable getBooleanVariable()
	{
		return mBooleanVariable;
	}


	private void setLabelFromState(final boolean lButtonState)
	{
		setSelected(lButtonState);
		if (lButtonState)
		{
			setText(mOnLabel);
		}
		else
		{
			setText(mOffLabel);
		}
	}

}
