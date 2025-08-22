import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert, Spinner } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import { reservationAPI, paymentAPI } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { toast } from 'react-toastify';
import { format } from 'date-fns';
import { FaCreditCard, FaLock, FaCheckCircle } from 'react-icons/fa';

const Payment = () => {
  const { reservationId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  
  const [reservation, setReservation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [paymentLoading, setPaymentLoading] = useState(false);
  const [paymentData, setPaymentData] = useState({
    cardNumber: '',
    expiryDate: '',
    cvv: '',
    cardHolderName: '',
    billingAddress: '',
    city: '',
    state: '',
    zipCode: '',
    paymentMethod: 'CREDIT_CARD'
  });

  useEffect(() => {
    loadReservationDetails();
  }, [reservationId]);

  const loadReservationDetails = async () => {
    try {
      const response = await reservationAPI.getReservationById(reservationId);
      setReservation(response.data);
    } catch (error) {
      toast.error('Failed to load reservation details');
      console.error('Error loading reservation:', error);
      navigate('/reservations');
    } finally {
      setLoading(false);
    }
  };

  const handlePaymentChange = (e) => {
    setPaymentData({
      ...paymentData,
      [e.target.name]: e.target.value
    });
  };

  const handlePaymentSubmit = async (e) => {
    e.preventDefault();
    setPaymentLoading(true);

    try {
      // Split expiry date into month and year
      const [expiryMonth, expiryYear] = paymentData.expiryDate.split('/');
      
      const paymentPayload = {
        reservationId: parseInt(reservationId),
        userId: user.userId,
        amount: reservation.totalCost,
        paymentMethod: paymentData.paymentMethod,
        cardNumber: paymentData.cardNumber.replace(/\s/g, ''), // Remove spaces
        cardHolderName: paymentData.cardHolderName,
        expiryMonth: expiryMonth,
        expiryYear: expiryYear,
        cvv: paymentData.cvv
      };

      await paymentAPI.processPayment(paymentPayload);
      toast.success('Payment processed successfully!');
      navigate('/reservations');
    } catch (error) {
      toast.error('Payment failed. Please try again.');
      console.error('Payment error:', error);
    } finally {
      setPaymentLoading(false);
    }
  };

  const formatCardNumber = (value) => {
    // Remove all non-digit characters
    const cleaned = value.replace(/\D/g, '');
    // Add spaces every 4 digits
    const formatted = cleaned.replace(/(\d{4})(?=\d)/g, '$1 ');
    return formatted;
  };

  const handleCardNumberChange = (e) => {
    const formatted = formatCardNumber(e.target.value);
    setPaymentData({
      ...paymentData,
      cardNumber: formatted
    });
  };

  const formatExpiryDate = (value) => {
    // Remove all non-digit characters
    const cleaned = value.replace(/\D/g, '');
    // Add slash after 2 digits
    if (cleaned.length >= 2) {
      return cleaned.substring(0, 2) + '/' + cleaned.substring(2, 4);
    }
    return cleaned;
  };

  const handleExpiryDateChange = (e) => {
    const formatted = formatExpiryDate(e.target.value);
    setPaymentData({
      ...paymentData,
      expiryDate: formatted
    });
  };

  if (loading) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <Spinner animation="border" role="status">
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </Col>
        </Row>
      </Container>
    );
  }

  if (!reservation) {
    return (
      <Container className="py-5">
        <Row>
          <Col className="text-center">
            <h3>Reservation not found</h3>
            <Button variant="primary" onClick={() => navigate('/reservations')}>
              Back to Reservations
            </Button>
          </Col>
        </Row>
      </Container>
    );
  }

  return (
    <Container className="py-4">
      <Row>
        <Col>
          <h2 className="mb-4">
            <FaCreditCard className="me-2" />
            Payment
          </h2>
        </Col>
      </Row>

      <Row>
        <Col lg={8}>
          <Card>
            <Card.Body>
              <h5 className="mb-3">Payment Information</h5>
              
              <Alert variant="info">
                <FaLock className="me-2" />
                Your payment information is secure and encrypted.
              </Alert>

              <Form onSubmit={handlePaymentSubmit}>
                <Row>
                  <Col md={8}>
                    <Form.Group className="mb-3">
                      <Form.Label>Card Number</Form.Label>
                      <Form.Control
                        type="text"
                        name="cardNumber"
                        value={paymentData.cardNumber}
                        onChange={handleCardNumberChange}
                        placeholder="1234 5678 9012 3456"
                        maxLength="19"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>CVV</Form.Label>
                      <Form.Control
                        type="text"
                        name="cvv"
                        value={paymentData.cvv}
                        onChange={handlePaymentChange}
                        placeholder="123"
                        maxLength="4"
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Card Holder Name</Form.Label>
                      <Form.Control
                        type="text"
                        name="cardHolderName"
                        value={paymentData.cardHolderName}
                        onChange={handlePaymentChange}
                        placeholder="John Doe"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label>Expiry Date</Form.Label>
                      <Form.Control
                        type="text"
                        name="expiryDate"
                        value={paymentData.expiryDate}
                        onChange={handleExpiryDateChange}
                        placeholder="MM/YY"
                        maxLength="5"
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <h6 className="mt-4 mb-3">Billing Address</h6>
                
                <Form.Group className="mb-3">
                  <Form.Label>Address</Form.Label>
                  <Form.Control
                    type="text"
                    name="billingAddress"
                    value={paymentData.billingAddress}
                    onChange={handlePaymentChange}
                    placeholder="123 Main Street"
                    required
                  />
                </Form.Group>

                <Row>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>City</Form.Label>
                      <Form.Control
                        type="text"
                        name="city"
                        value={paymentData.city}
                        onChange={handlePaymentChange}
                        placeholder="New York"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>State</Form.Label>
                      <Form.Control
                        type="text"
                        name="state"
                        value={paymentData.state}
                        onChange={handlePaymentChange}
                        placeholder="NY"
                        required
                      />
                    </Form.Group>
                  </Col>
                  <Col md={4}>
                    <Form.Group className="mb-3">
                      <Form.Label>ZIP Code</Form.Label>
                      <Form.Control
                        type="text"
                        name="zipCode"
                        value={paymentData.zipCode}
                        onChange={handlePaymentChange}
                        placeholder="10001"
                        required
                      />
                    </Form.Group>
                  </Col>
                </Row>

                <div className="d-flex gap-2 mt-4">
                  <Button
                    variant="secondary"
                    onClick={() => navigate('/reservations')}
                  >
                    Cancel
                  </Button>
                  <Button
                    variant="success"
                    type="submit"
                    disabled={paymentLoading}
                    className="flex-grow-1"
                  >
                    {paymentLoading ? (
                      <>
                        <Spinner animation="border" size="sm" className="me-2" />
                        Processing Payment...
                      </>
                    ) : (
                      <>
                        <FaCheckCircle className="me-2" />
                        Pay ₹{reservation.totalCost}
                      </>
                    )}
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>
        </Col>

        <Col lg={4}>
          <Card className="sticky-top" style={{ top: '20px' }}>
            <Card.Body>
              <h5 className="mb-3">Reservation Summary</h5>
              
              <div className="mb-2">
                <strong>Hotel:</strong> {reservation.hotelName || `Hotel #${reservation.hotelId}`}
              </div>
              
              <div className="mb-2">
                <strong>Dates:</strong><br />
                {format(new Date(reservation.checkInDate), 'MMM dd, yyyy')} - {' '}
                {format(new Date(reservation.checkOutDate), 'MMM dd, yyyy')}
              </div>
              
              <div className="mb-2">
                <strong>Guests:</strong> {reservation.numberOfGuests}
              </div>
              
              <div className="mb-2">
                <strong>Room Type:</strong> {reservation.roomType}
              </div>
              
              {reservation.specialRequests && (
                <div className="mb-2">
                  <strong>Special Requests:</strong><br />
                  <small className="text-muted">{reservation.specialRequests}</small>
                </div>
              )}
              
              <hr />
              
              <div className="d-flex justify-content-between">
                <strong>Total Amount:</strong>
                <strong className="text-success">₹{reservation.totalCost}</strong>
              </div>
              
              <div className="mt-3 p-2 bg-light rounded">
                <small className="text-muted">
                  <FaLock className="me-1" />
                  Secure payment powered by SSL encryption
                </small>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default Payment;
