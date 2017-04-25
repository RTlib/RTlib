package clearcontrol.stack.sourcesink.server;

import static java.lang.Math.toIntExact;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.Pair;

import clearcontrol.stack.StackRequest;
import clearcontrol.stack.sourcesink.StackSinkSourceInterface;
import gnu.trove.list.array.TDoubleArrayList;

/**
 * Stack server base
 *
 * @author royer
 */
public abstract class StackServerBase implements
                                      StackSinkSourceInterface,
                                      AutoCloseable
{
  private final ConcurrentHashMap<String, TDoubleArrayList> mChannelIndexToTimeStampInSecondsMap =
                                                                                                 new ConcurrentHashMap<>();

  private final ConcurrentHashMap<Pair<String, Long>, StackRequest> mChannelIndexToStackRequestMap =
                                                                                                   new ConcurrentHashMap<>();

  /**
   * Instantiates a stack server base
   */
  public StackServerBase()
  {
    super();
  }

  /**
   * Returns the number of stacks for the default channel
   * 
   * @return number of stacks
   */
  public long getNumberOfStacks()
  {
    return mChannelIndexToTimeStampInSecondsMap.get(cDefaultChannel)
                                               .size();
  }

  /**
   * Returns the number of stacks for a given channel
   * 
   * @param pChannel
   *          channel
   * 
   * @return number of stacks
   */
  public long getNumberOfStacks(String pChannel)
  {
    return mChannelIndexToTimeStampInSecondsMap.get(pChannel).size();
  }

  /**
   * Sets - for a given channel and stack index - the stack's time stamp in
   * seconds.
   * 
   * @param pChannel
   *          channel
   * @param pStackIndex
   *          stack index
   * @param pTimeStampInSeconds
   *          time stamp in seconds
   */
  public void setStackTimeStampInSeconds(String pChannel,
                                         final long pStackIndex,
                                         double pTimeStampInSeconds)
  {
    TDoubleArrayList lTimeStampList =
                                    mChannelIndexToTimeStampInSecondsMap.get(pChannel);

    if (lTimeStampList == null)
    {
      lTimeStampList = new TDoubleArrayList();
      mChannelIndexToTimeStampInSecondsMap.put(pChannel,
                                               lTimeStampList);
    }

    lTimeStampList.add(pTimeStampInSeconds);
  }

  /**
   * Returns the time stamp in seconds for a stack of given index and from the
   * default channel.
   * 
   * @param pStackIndex
   *          stack index
   * @return time stamp in seconds
   */
  public Double getStackTimeStampInSeconds(long pStackIndex)
  {
    return getStackTimeStampInSeconds(cDefaultChannel, pStackIndex);
  }

  /**
   * Returns - for a given channel and stack index - the stack's time stamp in
   * seconds
   * 
   * @param pChannel
   *          channel
   * 
   * @param pStackIndex
   *          stack index
   * @return stack's time stamp in seconds
   */
  public Double getStackTimeStampInSeconds(String pChannel,
                                           final long pStackIndex)
  {
    TDoubleArrayList lTimeStampList =
                                    mChannelIndexToTimeStampInSecondsMap.get(pChannel);

    if (lTimeStampList == null)
      return null;

    return new Double(lTimeStampList.get(toIntExact(pStackIndex)));
  }

  /**
   * Sets - for a given channel and stack index - the stack's request.
   * 
   * @param pChannel
   *          channel
   * @param pStackIndex
   *          stack index
   * @param pStackRequest
   *          stck request
   */
  public void setStackRequest(String pChannel,
                              final long pStackIndex,
                              StackRequest pStackRequest)
  {
    mChannelIndexToStackRequestMap.put(Pair.of(pChannel, pStackIndex),
                                       pStackRequest);
  }

  /**
   * Returns - for a given channel and stack index - the stack's request.
   * 
   * @param pChannel
   *          channel
   * 
   * @param pStackIndex
   *          stack index
   * @return stack's time stamp in seconds
   */
  public StackRequest getStackRequest(String pChannel,
                                      final long pStackIndex)
  {

    return mChannelIndexToStackRequestMap.get(Pair.of(pChannel,
                                                      pStackIndex));
  }

}
