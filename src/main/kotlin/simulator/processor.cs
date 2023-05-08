using System.Diagnostics;

namespace CombinedModelling
{
	using Time = System.Int32;
	using EventList = List<Event>;

	enum EventType
	{
		SIMULATION_FINISHED,
		GENERATION_FINISHED,
		PROCESSING_FINISHED
	}

	class Event
	{
		public Time time;
		public EventType type;
		public int blockIndex;

		public Event(Time time, EventType type, int blockIndex)
		{
			this.time = time;
			this.type = type;
			this.blockIndex = blockIndex;
		}
	}

	class RequestGenerator
	{
		public int totalRequests;
		public Time totalGenerationTime;

		private TimeGenerator timeGenerator;
		private RequestProcessor[] receivers;

		private Request? request;
		private bool busy;

		public RequestGenerator(TimeGenerator timeGenerator, RequestProcessor[] receivers)
		{
			this.timeGenerator = timeGenerator;
			this.receivers = receivers;

			this.totalRequests = 0;
			this.totalGenerationTime = 0;
			this.busy = false;
			this.request = null;
		}

		public Time? startGeneration(Time currTime)
		{
			if (busy)
				return null;
			busy = true;

			Time duration = generateDuration();
			Time eventTime = currTime + duration;

			totalGenerationTime += duration;
			request = new Request(eventTime);
			return eventTime;
		}

		public RequestProcessor? finishGeneration()
		{
			if (!busy)
				return null;
			busy = false;
			totalRequests += 1;

			int minQueueSize = receivers[0].queueSize();
			int minReceiverId = 0;
			for (int i = 0; i < receivers.Length; i++)
			{
				if (receivers[i].queueSize() < minQueueSize)
				{
					minQueueSize = receivers[i].queueSize();
					minReceiverId = i;
				}
			}
			receivers[minReceiverId].pushRequest(request);
			return receivers[minReceiverId];
		}

		public Time generateDuration()
		{
			return timeGenerator.randomTime();
		}

	}

	class RequestProcessor
	{
		public int totalRequests;
		public int totalProcessingTime;
		public int totalWaitingTime;


		private TimeGenerator timeGenerator;
		private Queue<Request> queue;
		// private Time waitingTime;
		private bool busy;
		private Request? currRequest;

		public RequestProcessor(TimeGenerator timeGenerator)
		{
			this.timeGenerator = timeGenerator;
			this.queue = new Queue<Request>();
			// this.waitingTime = 0;
			this.busy = false;

			this.totalRequests = 0;
			this.totalProcessingTime = 0;
			this.totalWaitingTime = 0;
		}

		public void pushRequest(Request request)
		{
			queue.Enqueue(request);
		}

		public int queueSize()
		{
			return queue.Count;
		}

		public Time? startProcessing(Time currTime)
		{
			if (busy || queue.Count == 0)
				return null;
			busy = true;

			currRequest = queue.Dequeue();
			Time duration = generateDuration();
			Time eventTime = currTime + duration;

			currRequest.timeOut = eventTime;
			totalWaitingTime += currTime - currRequest.timeIn;
			totalProcessingTime += duration;
			return eventTime;
		}

		public Request? finishProcessing()
		{
			if (!busy)
				return null;
			busy = false;
			totalRequests += 1;

			Request? request = currRequest;
			currRequest = null;
			return request;
		}

		public Time generateDuration()
		{
			return timeGenerator.randomTime();

		}
	}

	class Clock
	{
		public EventList[,] table;
		public EventList[] array;

		public int tableHeight;
		public int tableWidth;
		public int arraySize;

		public Time currentEnd;
		public Time currentTime;

		public Time time;

		public Clock(int arraySize, int tableWidth, Time time)
		{
			this.time = time;

			this.tableHeight = (int)(Math.Ceiling(Math.Log((double)(time) / (double)(arraySize), tableWidth)));
			this.tableWidth = tableWidth;
			this.arraySize = arraySize;

			this.table = new EventList[tableHeight, tableWidth];
			this.array = new EventList[arraySize];

			this.currentTime = 0;
			this.currentEnd = arraySize;

			for (int i = 0; i < this.tableHeight; i++)
				for (int j = 0; j < this.tableWidth; j++)
					this.table[i, j] = new EventList();

			for (int i = 0; i < this.arraySize; i++)
				this.array[i] = new EventList();

		}

		public void addEvent(Event e)
		{
			if (e.time > time)
				return;

			if (e.time <= currentEnd)
			{
				addEventToLevel(e, 0);
			}
			else
			{
				int level = getLevelByTime(e.time);
				int column = getColumnByLevelAndTime(level, e.time);
				table[level - 1, column - 1].Add(e);
			}
		}

		public void addEventToLevel(Event e, int level)
		{
			int column = getColumnByLevelAndTime(level, e.time);

			if (level == 0)
				array[column - 1].Add(e);
			else
				table[level - 1, column - 1].Add(e);
		}

		int getLevelByTime(Time time)
		{
			return (int)Math.Ceiling(Math.Log((double)time / (arraySize), tableWidth));
		}

		private int getColumnByLevelAndTime(int level, Time time)
		{
			if (level == 0)
				return (time - 1) % arraySize + 1;
			int granularity = getGranularity(level);
			return (int)Math.Ceiling((double)(time) / granularity);
		}

		public int getGranularity(int level)
		{
			if (level > tableHeight)
				throw new Exception("Level is too high");
			if (level == 0)
				return 1;
			return arraySize * (int)(Math.Pow(tableWidth, level - 1));
		}

		public void shiftEventsToLowerLevel(int level, int column)
		{
			if (level == 0)
				return;

			EventList eventList = table[level - 1, column - 1];
			foreach (Event e in eventList)
			{
				int level_ = level - 1;
				int time_ = (e.time - 1) % getGranularity(level) + 1;
				int column_ = getColumnByLevelAndTime(level_, time_);

				if (level_ == 0)
					array[column_ - 1].Add(e);
				else
					table[level_ - 1, column_ - 1].Add(e);
			}
			table[level - 1, column - 1].Clear();
		}

		public void print()
		{
			for (int i = 0; i < tableHeight; i++)
			{
				int n = tableHeight - i - 1;
				String tableRow = (n + 1) + ": ";
				for (int j = 0; j < tableWidth; j++)
					tableRow += (j + 1) + __formatList(table[n, j]);
				Console.WriteLine(tableRow);
			}

			String arrayRow = "0: ";
			for (int j = 0; j < arraySize; j++)
				arrayRow += (j + 1) + __formatList(array[j]);
			Console.WriteLine(arrayRow);
			Console.WriteLine();
		}

		private string __formatList(EventList list)
		{
			String listElems = "";
			int t = 0;
			foreach (Event e in list)
			{
				listElems += e.time;
				if (t != list.Count - 1)
					listElems += ", ";
				t++;
			}
			return "{" + listElems + "} ";
		}

	}

	class Model
	{
		Clock? clock;
		RequestGenerator[] generators;
		RequestProcessor[] processors;

		public Model(RequestGenerator[] generators, RequestProcessor[] processors)
		{
			this.generators = generators;
			this.processors = processors;
		}

		public void simulate(Time maxTime)
		{
			clock = new Clock(100, 10, maxTime);

			for (int n = 0; n < generators.Length; n++)
			{
				Time? eventTime = generators[n].startGeneration(0);
				if (eventTime != null)
				{
					clock.addEventToLevel(new Event((Time)eventTime, EventType.GENERATION_FINISHED, n), clock.tableHeight);
				}
			}
			clock.print();

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.Start();
			processLevel(clock.tableHeight);
			stopwatch.Stop();
			foreach (RequestGenerator gen in generators)
			{
				gen.totalGenerationTime = Math.Min(maxTime, gen.totalGenerationTime);
			}
			foreach (RequestProcessor proc in processors)
			{
				proc.totalProcessingTime = Math.Min(maxTime, proc.totalProcessingTime);
			}

			// Console.WriteLine("GENERATOR: {0} {1}", generators[0].totalGenerationTime, generators[0].totalRequests);
			// Console.WriteLine("PROCESSOR: {0} {1} {2}", processors[0].totalProcessingTime, processors[0].totalRequests, processors[0].totalWaitingTime / processors[0].totalRequests);
			Console.WriteLine("Elapsed time: {0} ms", stopwatch.ElapsedMilliseconds);
		}

		private void processLevel(int level)
		{
			if (level == 0)
			{
				clock.currentEnd = clock.currentTime + clock.arraySize;
				advanceTimeDeltaT();
				return;
			}
			else
			{
				int j = 0;
				while (j < clock.tableWidth)
				{
					if (clock.table[level - 1, j].Count == 0)
					{
						clock.currentTime += clock.getGranularity(level);
					}
					else
					{
						int retries = 0;
						while (clock.table[level - 1, j].Count != 0)
						{
							if (retries > 0)
								clock.currentTime -= clock.getGranularity(level);
							clock.shiftEventsToLowerLevel(level, j + 1);
							processLevel(level - 1);
							retries++;
						}
					}
					j++;
				}
			}
		}

            private void advanceTimeDeltaT()
		{
			for (int j = 0; j < clock.arraySize; j++)
			{
				clock.currentTime++;
				int k = 0;
				while (k < clock.array[j].Count)
				{
					processEvent(clock.array[j][k]);
					k++;
				}
				clock.array[j].Clear();
			}
		}

		private void processEvent(Event e)
		{
			// Console.WriteLine("Process Event. CurrTime: {0}. Event {1} {2}", clock.currentTime, e.time, e.type);
			if (e.time != clock.currentTime)
				throw new Exception("Event time doesn't match current time!");

			if (e.type == EventType.GENERATION_FINISHED)
			{
				RequestGenerator generator = generators[e.blockIndex];
				RequestProcessor? processor = generator.finishGeneration();
				Time? time = generator.startGeneration(clock.currentTime);
				if (time != null)
					clock.addEvent(new Event((Time)time, EventType.GENERATION_FINISHED, e.blockIndex));

				time = processor?.startProcessing(e.time);
				if (time != null)
					clock.addEvent(new Event((Time)time, EventType.PROCESSING_FINISHED, e.blockIndex));
			}
			else if (e.type == EventType.PROCESSING_FINISHED)
			{
				RequestProcessor processor = processors[e.blockIndex];
				processor.finishProcessing();
				Time? time = processor.startProcessing(clock.currentTime);
				if (time != null)
					clock.addEvent(new Event((Time)time, EventType.PROCESSING_FINISHED, e.blockIndex));
			}
		}

	}


}
