import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, Button, Modal, Badge, Spinner } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { hotelAPI } from '../../services/api';
import { toast } from 'react-toastify';
import { FaPlus, FaEdit, FaTrash, FaEye, FaStar, FaRupeeSign } from 'react-icons/fa';

const ManageHotels = () => {
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedHotel, setSelectedHotel] = useState(null);

  useEffect(() => {
    loadHotels();
  }, []);

  const loadHotels = async () => {
    try {
      const response = await hotelAPI.getAllHotels();
      // Ensure we always set an array
      setHotels(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      toast.error('Failed to load hotels');
      console.error('Error loading hotels:', error);
      // Set empty array on error
      setHotels([]);
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteHotel = (hotel) => {
    setSelectedHotel(hotel);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    setDeleteLoading(true);
    try {
      await hotelAPI.deleteHotel(selectedHotel.hotelId);
      toast.success('Hotel deleted successfully!');
      setShowDeleteModal(false);
      loadHotels(); // Reload the list
    } catch (error) {
      toast.error('Failed to delete hotel');
      console.error('Delete error:', error);
    } finally {
      setDeleteLoading(false);
    }
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

  return (
    <div className="admin-panel">
      <Container className="py-4">
        <Row>
          <Col>
            <div className="d-flex justify-content-between align-items-center mb-4">
              <h2>Manage Hotels</h2>
              <Link to="/admin/hotels/add" className="btn btn-primary">
                <FaPlus className="me-2" />
                Add New Hotel
              </Link>
            </div>
          </Col>
        </Row>

        <Row>
          <Col>
            <Card>
              <Card.Header>
                <h5 className="mb-0">All Hotels ({hotels.length})</h5>
              </Card.Header>
              <Card.Body>
                {hotels.length === 0 ? (
                  <div className="text-center py-5">
                    <h5>No hotels found</h5>
                    <p className="text-muted">Start by adding your first hotel to the system.</p>
                    <Link to="/admin/hotels/add" className="btn btn-primary">
                      <FaPlus className="me-2" />
                      Add Hotel
                    </Link>
                  </div>
                ) : (
                  <Table responsive hover>
                    <thead>
                      <tr>
                        <th>ID</th>
                        <th>Hotel Name</th>
                        <th>Location</th>
                        <th>Rating</th>
                        <th>Room Rates</th>
                        <th>Room Availability</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {Array.isArray(hotels) && hotels.length > 0 ? hotels.map((hotel) => (
                        <tr key={hotel.hotelId}>
                          <td>#{hotel.hotelId}</td>
                          <td>
                            <strong>{hotel.hotelName}</strong>
                            <br />
                            <small className="text-muted">
                              {hotel.description?.substring(0, 50)}
                              {hotel.description?.length > 50 ? '...' : ''}
                            </small>
                          </td>
                          <td>
                            {hotel.city}, {hotel.state}
                            <br />
                            <small className="text-muted">{hotel.country}</small>
                          </td>
                          <td>
                            <Badge bg="success">
                              <FaStar className="me-1" />
                              {hotel.avgRatingByCustomers || 'N/A'}
                            </Badge>
                          </td>
                          <td>
                            <div className="small">
                              <FaRupeeSign className="me-1 text-success" />
                              AC: ₹{hotel.acRoomCost}
                              <br />
                              <FaRupeeSign className="me-1 text-success" />
                              Non-AC: ₹{hotel.nonAcRoomCost}
                            </div>
                          </td>
                          <td>
                            <div className="small">
                              <div className="mb-1">
                                <strong>AC:</strong> {hotel.availableAcRooms || 0}/{hotel.totalAcRooms || 0}
                              </div>
                              <div>
                                <strong>Non-AC:</strong> {hotel.availableNonAcRooms || 0}/{hotel.totalNonAcRooms || 0}
                              </div>
                            </div>
                          </td>
                          <td>
                            <div className="d-flex gap-1">
                              <Link
                                to={`/hotel/${hotel.hotelId}`}
                                className="btn btn-outline-info btn-sm"
                                title="View Details"
                              >
                                <FaEye />
                              </Link>
                              <Link
                                to={`/admin/hotels/edit/${hotel.hotelId}`}
                                className="btn btn-outline-primary btn-sm"
                                title="Edit Hotel"
                              >
                                <FaEdit />
                              </Link>
                              <Button
                                variant="outline-danger"
                                size="sm"
                                onClick={() => handleDeleteHotel(hotel)}
                                title="Delete Hotel"
                              >
                                <FaTrash />
                              </Button>
                            </div>
                          </td>
                        </tr>
                      )) : (
                        <tr>
                          <td colSpan="6" className="text-center py-4">
                            <p className="text-muted mb-0">
                              {loading ? 'Loading hotels...' : 'No hotels available'}
                            </p>
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </Table>
                )}
              </Card.Body>
            </Card>
          </Col>
        </Row>

        {/* Delete Confirmation Modal */}
        <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
          <Modal.Header closeButton>
            <Modal.Title>Delete Hotel</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <p>Are you sure you want to delete this hotel?</p>
            {selectedHotel && (
              <div className="bg-light p-3 rounded">
                <strong>Hotel Details:</strong>
                <br />
                <strong>Name:</strong> {selectedHotel.hotelName}
                <br />
                <strong>Location:</strong> {selectedHotel.city}, {selectedHotel.state}, {selectedHotel.country}
                <br />
                <strong>ID:</strong> #{selectedHotel.hotelId}
              </div>
            )}
            <p className="text-danger mt-2">
              <small>
                <strong>Warning:</strong> This action cannot be undone. All associated reservations may be affected.
              </small>
            </p>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
              Cancel
            </Button>
            <Button 
              variant="danger" 
              onClick={confirmDelete} 
              disabled={deleteLoading}
            >
              {deleteLoading ? 'Deleting...' : 'Delete Hotel'}
            </Button>
          </Modal.Footer>
        </Modal>
      </Container>
    </div>
  );
};

export default ManageHotels;
