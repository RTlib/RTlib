package rtlib.stack.server;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;

import rtlib.core.recycling.Recycler;
import rtlib.core.variable.VariableInterface;
import rtlib.core.variable.bundle.VariableBundle;
import rtlib.stack.Stack;

public class StackRAMServer	implements
														StackSinkInterface,
														StackSourceInterface
{

	ArrayList<Stack> mStackList = new ArrayList<Stack>();
	TLongArrayList mStackTimePointList = new TLongArrayList();

	protected final VariableBundle mMetaDataVariableBundle = new VariableBundle("MetaData");

	public StackRAMServer()
	{
		super();
	}

	@Override
	public boolean update()
	{
		return true;
	}

	@Override
	public long getNumberOfStacks()
	{
		return mStackList.size();
	}

	@Override
	public void setStackRecycler(final Recycler<Stack, Long> pStackRecycler)
	{
	}

	@Override
	public Stack getStack(final long pStackIndex)
	{
		return mStackList.get((int) pStackIndex);
	}

	@Override
	public double getStackTimeStampInSeconds(final long pStackIndex)
	{
		return mStackTimePointList.get((int) pStackIndex);
	}

	@Override
	public boolean appendStack(final Stack pStack)
	{
		mStackTimePointList.add(pStack.getTimeStampInNanoseconds());
		return mStackList.add(pStack);
	}

	@Override
	public void addMetaDataVariable(final String pPrefix,
																	final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundle.addVariable(pVariable);
	}

	@Override
	public void removeAllMetaDataVariables()
	{
		mMetaDataVariableBundle.removeAllVariables();
	}

	@Override
	public void removeMetaDataVariable(final VariableInterface<?> pVariable)
	{
		mMetaDataVariableBundle.removeVariable(pVariable);

	}

}