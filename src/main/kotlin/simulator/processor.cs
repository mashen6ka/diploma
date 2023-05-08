	enum EventType
	{
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


	class Model // timeBased
	{
		RequestGenerator[] generators;
		RequestProcessor[] processors;

		Time delta = 1;
		public Model(RequestGenerator[] generators, RequestProcessor[] processors)
		{
			this.generators = generators;
			this.processors = processors;
		}

		public void simulate(Time maxTime)
		{
			// for (int n = 0; n < generators.Length; n++)
			// {
			// 	generators[n].startGeneration(0);
			// 	Console.WriteLine(generators[n].next);
			// }

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.Start();
			Time currTime = 0;
			while (currTime < maxTime)
			{
				foreach (RequestGenerator gen in generators)
				{
					if (gen.next <= currTime)
					{
						RequestProcessor? proc = gen.finishGeneration();
						if (proc != null)
						{
							// Console.WriteLine("Process Event. GEN. CurrTime: {0}. Event {1}", currTime, gen.next);
							proc.startProcessing(currTime);
						}
						gen.startGeneration(currTime);
					}
				}

				foreach (RequestProcessor proc in processors)
				{
					if (proc.next <= currTime)
					{
						if (proc.busy)
						{
							// Console.WriteLine("Process Event. PROC. CurrTime: {0}. Event {1}", currTime, proc.next);
							proc.finishProcessing();
							proc.startProcessing(currTime);
						}
					}
				}

				currTime += delta;
			}
			stopwatch.Stop();

			foreach (RequestGenerator gen in generators)
			{
				gen.totalGenerationTime = Math.Min(maxTime, gen.totalGenerationTime);
			}
			foreach (RequestProcessor proc in processors)
			{
				proc.totalProcessingTime = Math.Min(maxTime, proc.totalProcessingTime);
			}
			Console.WriteLine("Elapsed time: {0} ms", stopwatch.ElapsedMilliseconds);
		}
	}



	
	class Model // eventBased
	{
		RequestGenerator[] generators;
		RequestProcessor[] processors;

		PriorityQueue<Event, Time> eventList;

		public Model(RequestGenerator[] generators, RequestProcessor[] processors)
		{
			this.generators = generators;
			this.processors = processors;

			this.eventList = new PriorityQueue<Event, Time>();
		}

		public void simulate(Time maxTime)
		{
			for (int n = 0; n < generators.Length; n++)
			{
				Time? eventTime = generators[n].startGeneration(0);
				if (eventTime != null)
				{
					eventList.Enqueue(new Event((Time)eventTime, EventType.GENERATION_FINISHED, n), (Time)eventTime);
				}
			}

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.Start();
			Time currTime = 0;
			while (currTime < maxTime)
			{
				Event e = eventList.Dequeue();
				currTime = e.time;
				// Console.WriteLine("Process Event. CurrTime: {0}. Event {1} {2}", currTime, e.time, e.type);
				if (e.type == EventType.GENERATION_FINISHED)
				{
					RequestGenerator generator = generators[e.blockIndex];
					RequestProcessor? processor = generator.finishGeneration();
					Time? time = generator.startGeneration(currTime);
					if (time != null)
						eventList.Enqueue(new Event((Time)time, EventType.GENERATION_FINISHED, e.blockIndex), (Time)time);

					time = processor?.startProcessing(e.time);
					if (time != null)
						eventList.Enqueue(new Event((Time)time, EventType.PROCESSING_FINISHED, e.blockIndex), (Time)time);
				}
				else if (e.type == EventType.PROCESSING_FINISHED)
				{
					RequestProcessor processor = processors[e.blockIndex];
					processor.finishProcessing();
					Time? time = processor.startProcessing(currTime);
					if (time != null)
						eventList.Enqueue(new Event((Time)time, EventType.PROCESSING_FINISHED, e.blockIndex), (Time)time);
				}
			}
			stopwatch.Stop();
			foreach (RequestGenerator gen in generators)
			{
				gen.totalGenerationTime = Math.Min(maxTime, gen.totalGenerationTime);
			}
			foreach (RequestProcessor proc in processors)
			{
				proc.totalProcessingTime = Math.Min(maxTime, proc.totalProcessingTime);
			}
			Console.WriteLine("Elapsed time: {0} ms", stopwatch.ElapsedMilliseconds);
		}
	}
