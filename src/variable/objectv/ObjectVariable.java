package variable.objectv;

import java.util.concurrent.CopyOnWriteArrayList;

public class ObjectVariable<O>	implements
																ObjectInputOutputVariableInterface<O>
{
	protected volatile O mReference;
	private ObjectInputVariableInterface<O> mInputVariable;
	private CopyOnWriteArrayList<ObjectInputVariableInterface<O>> mInputVariables;

	private ObjectOutputVariableInterface<O> mOutputVariable;

	public ObjectVariable()
	{
		mReference = null;
	}

	public ObjectVariable(final O pReference)
	{
		super();
		mReference = pReference;
	}

	public final void setReference(final O pNewReference)
	{
		setReference(this, pNewReference);
	}

	@Override
	public void setReference(	final Object pObjectEventSource,
														final O pNewReference)
	{

		if (mInputVariable != null)
			mInputVariable.setReference(pObjectEventSource, pNewReference);
		else if (mInputVariables != null)
		{
			for (final ObjectInputVariableInterface<O> lObjectInputVariableInterface : mInputVariables)
			{
				lObjectInputVariableInterface.setReference(	pObjectEventSource,
																										pNewReference);
			}
		}

		mReference = pNewReference;
	}

	public void setCurrentReference(final Object pObjectEventSource)
	{
		setReference(pObjectEventSource, mReference);
	}

	@Override
	public O getReference()
	{
		if (mOutputVariable != null)
			mReference = mOutputVariable.getReference();

		return mReference;
	}

	public final void sendUpdatesTo(final ObjectInputVariableInterface<O> pObjectVariable)
	{
		synchronized (this)
		{
			if (mInputVariable == null && mInputVariables == null)
			{
				mInputVariable = pObjectVariable;
			}
			else if (mInputVariable != null && mInputVariables == null)
			{
				mInputVariables = new CopyOnWriteArrayList<ObjectInputVariableInterface<O>>();
				mInputVariables.add(mInputVariable);
				mInputVariables.add(pObjectVariable);
				mInputVariable = null;
			}
			else if (mInputVariable == null && mInputVariables != null)
			{
				mInputVariables.add(pObjectVariable);
			}
		}
	}

	public void stopSendUpdatesTo(final ObjectInputVariableInterface<O> pObjectVariable)
	{
		synchronized (this)
		{
			if (mInputVariable != null && mInputVariable == pObjectVariable
					&& mInputVariables == null)
			{
				mInputVariable = null;
			}
			else if (mInputVariable == null && mInputVariables != null)
			{
				mInputVariables.remove(pObjectVariable);
			}
		}
	}

	public final void sendQueriesTo(final ObjectOutputVariableInterface pObjectVariable)
	{
		mOutputVariable = pObjectVariable;
	}

	public final void syncWith(final ObjectInputOutputVariableInterface pObjectVariable)
	{
		sendUpdatesTo(pObjectVariable);
		if (mOutputVariable != null)
		{
			throw new UnsupportedOperationException("Cannot sync a variable twice!");
		}
		sendQueriesTo(pObjectVariable);
	}

	public boolean isNotNull()
	{
		return mReference != null;
	}

	public boolean isNull()
	{
		return mReference == null;
	}

}
