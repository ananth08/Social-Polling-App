package edu.iastate.cs510.company2.testsupport;

import edu.iastate.cs510.company2.gateway.Message;
import edu.iastate.cs510.company2.gateway.PsGateway;

/**
 * @author RobertW
 *
 * This facade is meant to "front" a mock implementation of a persistence service gateway. We expect this
 * facade will normally be configured to run with a Mockito generated mock, it has no specific dependencies on 
 * the Mockito interface, so it can also be used with hand-coded mocks or mocks generated by some other 
 * tool. 
 *
 * @param <T> 
 */
public class MockPsGatewayFacade <T extends PsGateway>implements PsGateway, MockTestControl<T> {

	public T mock = null; 
	
	@Override
	public Response send(Message msg) {
		if (mock == null)
			throw new IllegalStateException("Must install mock implentation before using this gateway");
		else return mock.send( msg);
	}

	@Override
	public void  setMockInstance(T mock) {
		this.mock = mock;
		
	}

	@Override
	public T getMockInstance() {
		return mock;
	}

}
